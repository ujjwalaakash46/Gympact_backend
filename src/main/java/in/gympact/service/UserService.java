package in.gympact.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

import in.gympact.entities.Attendance;
import in.gympact.entities.CurrentPackage;
import in.gympact.entities.Diet;
import in.gympact.entities.Gym;
import in.gympact.entities.Message;
import in.gympact.entities.Package;
import in.gympact.entities.PastWorkout;
import in.gympact.entities.Progress;
import in.gympact.entities.User;
import in.gympact.entities.Workout;
import in.gympact.enums.Enum.Role;
import in.gympact.exception.CustomException;
import in.gympact.pojo.DiffProgress;
import in.gympact.pojo.Login;
import in.gympact.pojo.PersonalBest;
import in.gympact.pojo.Signup;
import in.gympact.respositry.AttendanceRepositry;
import in.gympact.respositry.CurrentPackageRepositry;
import in.gympact.respositry.DietRepositry;
import in.gympact.respositry.GymRepositry;
import in.gympact.respositry.MessageRepositry;
import in.gympact.respositry.PackageRepositry;
import in.gympact.respositry.PastWorkoutRepositry;
import in.gympact.respositry.ProgressRepositry;
import in.gympact.respositry.UserRepositry;
import in.gympact.respositry.WorkoutRepositry;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class UserService {
	
	@Autowired
	private AmazonS3 s3Client;
	
	@Value("${bucketName}")
	private String bucketName;
	
	@Autowired
	UserRepositry userRepo;
	
	@Autowired
	WorkoutRepositry workoutRepo;

	@Autowired
	AttendanceRepositry attendanceRepo;
	
	@Autowired
	ProgressRepositry progressRepo;
	
	@Autowired
	PastWorkoutRepositry pastWorkoutRepo;
	
	@Autowired
	MessageRepositry messageRepo;
	
	@Autowired
	GymRepositry gymRepo;
	
	@Autowired
	CurrentPackageRepositry currentPackageRepo;
	
	@Autowired
	PackageRepositry packageRepo;
	
	@Autowired
	DietRepositry dietRepo;
	
	@Value("${project.image}")
	String filePath;
	
	
	public User addUser(int gymId, User user) {
//		User user = new User();
		
		if(user.getId()==null) {
			
			List<User> userWithSameNumber = userRepo.findByPhone(user.getPhone());
			Assert.isTrue(userWithSameNumber.size()==0, "Phone number already in Use");
			
			user.setJoinOn(LocalDateTime.now());
			user.setLastVisit(LocalDateTime.now());
			user.setCurrentStreak(0);
			user.setHighestStreak(0);
			user.setHighestStreakThisMonth(0);
		}else {
			Optional<User> userOptional = userRepo.findById(user.getId());
			user.setPastWorkoutList(userOptional.get().getPastWorkoutList());
			user.setProgressList(userOptional.get().getProgressList());
		}
		
		List<Workout> workoutList = new LinkedList<>();
		for(Workout w: user.getWorkoutList()) {
			Workout workout = workoutRepo.findById(w.getId()).get();
			workoutList.add(workoutRepo.save(workout));
		}
		user.setWorkoutList(workoutList);
		
		Diet diet = dietRepo.findById(user.getDiet().getId()).get();
		user.setDiet(diet);
		
		Gym gym = gymRepo.findById(gymId).get();
		user.setGym(gym);
		
		
		if(user.getCurrentPackage().getId()==null) {
			CurrentPackage currentPackage = new CurrentPackage();
			Package pack =  packageRepo.findById(user.getCurrentPackage().getPackageDetails().getId()).get();
			currentPackage.setPackageDetails(pack);
			currentPackage.setStartDate(user.getCurrentPackage().getStartDate());
			currentPackage.setEndDate(user.getCurrentPackage().getEndDate());
			currentPackage = currentPackageRepo.save(currentPackage);
			user.setCurrentPackage(currentPackage);
			if(user.getCurrentPackage().getStartDate().isBefore(LocalDateTime.now())) {
				user.setJoinOn(user.getCurrentPackage().getStartDate());
			}
		}else {
			Package pack =  packageRepo.findById(user.getCurrentPackage().getPackageDetails().getId()).get();
			CurrentPackage currentPackage = user.getCurrentPackage();
			currentPackage.setPackageDetails(pack);
			currentPackage.setStartDate(user.getCurrentPackage().getStartDate());
			currentPackage.setEndDate(user.getCurrentPackage().getEndDate());
			user.setCurrentPackage(currentPackage);
		}
		

		
//		for(PastWorkout p : user.getPastWorkoutList()) {
//			if(p.getId()==null) {
//				p.setUser(user);
//				pastWorkoutRepo.save(p);
//			}
//		}
		
		return userRepo.save(user);
	}
	
	public User getUserById(int id) {
		Optional<User> user = userRepo.findById(id);
		Assert.isTrue(user.isPresent(), "Can't find the user");
		return user.get();
	}
	
	public Attendance getAttendance(int id) {
		Optional<User> userOptional = userRepo.findById(id);
		Assert.isTrue(userOptional.isPresent(), "Can't find the user");
		
		User user = userOptional.get();
		LocalDateTime todaysDate = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT);
		if(user.getCurrentPackage() !=null && user.getCurrentPackage().getEndDate().compareTo(todaysDate)<0) {
			throw new CustomException("PACKAGE_ENDED", HttpStatus.NOT_ACCEPTABLE);
		}
		Attendance todaysAttendance = attendanceRepo.findTopByUserAndDateTimeGreaterThanEqualOrderByDateTime(user, LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT));
		
		return todaysAttendance;
	}

	public Attendance markAttendance(int id) {
		Optional<User> userOptional = userRepo.findById(id);
		User user = userOptional.get();
		LocalDateTime todaysDate = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT);
		if(user.getCurrentPackage() !=null && user.getCurrentPackage().getEndDate().compareTo(todaysDate)<0) {
			throw new CustomException("PACKAGE_ENDED", HttpStatus.NOT_ACCEPTABLE);
		}
		Attendance todaysAttendance = attendanceRepo.findTopByUserAndDateTimeGreaterThanEqualOrderByDateTime(user, LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT));
		if(todaysAttendance==null) {
			
			List<Attendance> attList = user.getAttendanceList();
			Attendance attendance = new Attendance();
			attendance.setDateTime(LocalDateTime.now());
			attList.add(attendance);
		
			user.setAttendanceList(attList);
			user.setLastVisit(LocalDateTime.now());
			
			int currentStreak = user.getCurrentStreak()+1;
			int highestStreakThisMonth = user.getHighestStreakThisMonth()+1;
			int highestStreak = currentStreak>highestStreakThisMonth?currentStreak:highestStreakThisMonth;
			
			user.setCurrentStreak(currentStreak);
			user.setHighestStreakThisMonth(highestStreakThisMonth);
			user.setHighestStreak(user.getHighestStreak()>highestStreak?user.getHighestStreak():highestStreak);
			attendance.setUser(user);
			return attendanceRepo.save(attendance);
		}else {
			return todaysAttendance;
		}
		
//		return "Attendance Marked";
	}

	public String addProgress(int id, Progress newProgress) {
		Optional<User> userOptional = userRepo.findById(id);
		Assert.isTrue(userOptional.isPresent(), "Can't find the user");
		User user = userOptional.get();
		
		Progress todaysOldProgress = progressRepo.findTopByUserAndDateTimeGreaterThanEqualOrderByDateTime(user, LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT));
		if(todaysOldProgress!=null) {
			newProgress.setId(todaysOldProgress.getId());
			progressRepo.save(newProgress);
			return "Progress Updated";
		}else {
			List<Progress> progList = user.getProgressList();
			if(progList==null) {
				progList=new LinkedList<>();			
			}
			progList.add(newProgress);
			user.setProgressList(progList);
			user.setCoin(user.getCoin()+5);
			newProgress.setUser(user);
			
			progressRepo.save(newProgress);
			return "Progress Added";
		}
	}

	public List<Progress> getUserProgress(int id) {
		Optional<User> userOptional = userRepo.findById(id);
		Assert.isTrue(userOptional.isPresent(), "Can't find the user");
		User user = userOptional.get();
		return progressRepo.findByUserOrderByDateTimeDesc(user);
	}

	public Diet getDiet(int id) {
		Optional<User> userOptional = userRepo.findById(id);
		Assert.isTrue(userOptional.isPresent(), "Can't find the user");
		User user = userOptional.get();
		return user.getDiet();
	}

	public CurrentPackage getCurrentPackage(int id) {
		Optional<User> userOptional = userRepo.findById(id);
		Assert.isTrue(userOptional.isPresent(), "Can't find the user");
		User user = userOptional.get();
		
		return user.getCurrentPackage();
	}

	public String savePastWorkout(int id, PastWorkout pastWorkout) {
		Optional<User> userOptional = userRepo.findById(id);
		Assert.isTrue(userOptional.isPresent(), "Can't find the user");
		User user = userOptional.get();
		
		List<PastWorkout> pastWorkoutList = user.getPastWorkoutList();
		pastWorkoutList.add(pastWorkout);
		user.setCoin(user.getCoin()+10);
		user.setPastWorkoutList(pastWorkoutList);
		pastWorkout.setUser(user);
		
		pastWorkoutRepo.save(pastWorkout);
		return "Saved";
	}

	public List<PastWorkout> getPastWorkouts(int id) {
		Optional<User> userOptional = userRepo.findById(id);
		Assert.isTrue(userOptional.isPresent(), "Can't find the user");
		User user = userOptional.get();
		return user.getPastWorkoutList();
	}

	public List<Message> getNotification(int id) {
		Optional<User> userOptional = userRepo.findById(id);
		Assert.isTrue(userOptional.isPresent(), "Can't find the user");
		User user = userOptional.get();
		System.out.println(user.getNotifications());
		return user.getNotifications();
	}

	public List<User> searchUser(String query, int gymId) {
		Optional<Gym> gymOptional = gymRepo.findById(gymId);
		Assert.isTrue(gymOptional.isPresent(), "Can't find the gym");
		Gym gym = gymOptional.get();
		
		List<User> userSearchList = userRepo.findByNameOrPhone(query, query, gym, Role.member);
		return userSearchList;
	}

	public User checkPhone(String phone) {
//		List<User> userSearchList = userRepo.findByPhoEquals(phone.toString());
		List<User> userSearchList = userRepo.findByPhone(phone);
		
		if(userSearchList.size()==0) {
			throw new CustomException("No User Found", HttpStatus.BAD_REQUEST);
		}
//		Assert.isTrue(userSearchList.size()>0, "No User Found");
//		new ResponseEntity<ResponseDto>()
		return userSearchList.get(0);
	}

	public User signup(Signup signup) {
		Optional<User> userOptional = userRepo.findById(signup.userId);
		Assert.isTrue(userOptional.isPresent(), "Can't find the user");
		User user = userOptional.get();
		
		if(signup.password.isEmpty() || signup.password.isBlank()) {
			throw new CustomException("Enter valid password", HttpStatus.BAD_REQUEST);
		}
		if(!signup.password.equals(signup.confirmPassword)) {
			throw new CustomException("Password and Confirm Password are not same", HttpStatus.BAD_REQUEST);
		}
		if(userRepo.findByEmail(signup.email).size()!=0) {
			throw new CustomException("Email already in use", HttpStatus.BAD_REQUEST);
		}

		user.setEmail(signup.email);
		user.setPassword(signup.password);
		return userRepo.save(user);
	}

	public User login(Login login) {
		Optional<User> userOptional = userRepo.findById(login.userId);
		Assert.isTrue(userOptional.isPresent(), "Can't find the user");
		User user = userOptional.get();
		
		if(!login.email.equals(user.getEmail())){
			throw new CustomException("Email is not same", HttpStatus.BAD_REQUEST);
		}
		if(!login.password.equals(user.getPassword())){
			throw new CustomException("Password is Incorrect", HttpStatus.BAD_REQUEST);
		}
		
		return user;
	}

	public List<DiffProgress> highAchieverList(int gymId) {
		Optional<Gym> gymOptional = gymRepo.findById(gymId);
		Assert.isTrue(gymOptional.isPresent(), "Can't find the gym");
		Gym gym = gymOptional.get();
		
		LocalDateTime thisMonthStartDate = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT).withDayOfMonth(1);
		List<DiffProgress> finalList = new LinkedList<>();
		List<User> activeUserlist = userRepo.findByGymAndRoleAndLastVisitGreaterThanEqual(gym, Role.member, thisMonthStartDate);
		for(User u : activeUserlist) {
			Progress firstInMonthProgress = progressRepo.findTopByUserAndDateTimeGreaterThanEqualOrderByDateTime(u, thisMonthStartDate);
			Progress lastProgress = progressRepo.findTopByUserAndDateTimeGreaterThanEqualOrderByDateTimeAsc(u, thisMonthStartDate);
			if(lastProgress!=null && firstInMonthProgress!=null) {
				DiffProgress dp = new DiffProgress();
				dp.weight=firstInMonthProgress.getWeight()-lastProgress.getWeight();
				dp.name=u.getName();
				finalList.add(dp);
			}
		}
		finalList.sort((o1, o2) -> (int)(o1.weight-o2.weight)) ;
		return finalList;
	}

	public PersonalBest personalBest(int id) {
		Optional<User> userOptional = userRepo.findById(id);
		Assert.isTrue(userOptional.isPresent(), "Can't find the user");
		User user = userOptional.get();
		
		Progress earliestProgress = progressRepo.findTopByUserOrderByDateTimeAsc(user);
		Progress latestProgress = progressRepo.findTopByUserOrderByDateTimeAsc(user);

		Assert.isTrue(earliestProgress!=null && latestProgress!=null, "No progress found");
		
		PersonalBest personalBest = new PersonalBest();
		personalBest.weigthDiff = earliestProgress.getWeight() - latestProgress.getWeight();
		personalBest.fatDiff = earliestProgress.getFat() - latestProgress.getFat();
		personalBest.calBurnDiff = earliestProgress.getCalBurn() - latestProgress.getCalBurn();
		personalBest.waterIntakeDiff = earliestProgress.getWaterIntake() - latestProgress.getWaterIntake();
		
		return personalBest;
	}

	public Map<String,List<String>> homeMessagge(int id) {
		Optional<User> userOptional = userRepo.findById(id);
		Assert.isTrue(userOptional.isPresent(), "Can't find the user");
		User user = userOptional.get();
		
//		LocalDateTime now = LocalDateTime.now();
//		LocalDateTime noon = LocalDateTime.of(LocalDate.now(), LocalTime.NOON);
//		
//		HomeMessage homeMessage = new HomeMessage();
//		if(now.isBefore(noon)) {
//			homeMessage.greeting="Good Morning";
//		}else {
//			homeMessage.greeting="Good Evening";
//			
//		}
		
		List<String> intro = new LinkedList<>();
		List<String> workout = new LinkedList<>();
		List<String> appreciate = new LinkedList<>();
		List<String> movitaional = new LinkedList<>();
//		List<String> facts = new LinkedList<>();
		
		intro.add("Lets gooo champ! Make you entry and Start your workout");
		intro.add("Welcome Champ! Make you entry and Also entry you progress in Progress section");
		intro.add("Great to see you champ! Make you entry and Start your workout");
		
		
		workout.add("Lets start your routine and finish it like a pro as you are!");
		workout.add("Lets start your routine now! Push Pass Your Limits");
		workout.add("Lets start your routine! Today you go beyond your limits");

		appreciate.add("Great work Champ!, keep it up bro");
		appreciate.add("Amazing work! we can see your Progress");
		appreciate.add("Inspirational! Great work Champ!");
		
		movitaional.add("Push Past your limits Champ!, you can do it");
		movitaional.add("Lets show the world who you are!");
		movitaional.add("Turn On the Beast Mode!");
		movitaional.add("You did not wake up today to be mediocre.");
		movitaional.add("The real workout starts when you want to stop.");
		movitaional.add("A champion is someone who gets up when they cant.");
		movitaional.add("What hurts today makes you stronger tomorrow");
		
		
		Map<String, List<String>> result = new HashMap<>();
		result.put("intro", intro);
		result.put("workout", workout);
		result.put("appreciate", appreciate);
		result.put("movitaional", movitaional);
//		System.out.println(result);
		
		return result;
	}
	
	public void postCon() {
		List<User> userList = userRepo.findAll();
		int n = 1;
		for(User u : userList ) {
			if(n<=3)
			u.setDob(LocalDateTime.of(2023, 8, 3,0, 0));
			n++;
			//CurrentPackage c =u.getCurrentPackage();
//			c.setEndDate(LocalDateTime.of(2023, 8, 4, 5, 5));
//			u.setCurrentPackage(c);
			userRepo.save(u);
		}
		System.out.println("done");
	}

	public List<User> birthdayList(int gymId) {
		Optional<Gym> gymOptional = gymRepo.findById(gymId);
		Assert.isTrue(gymOptional.isPresent(), "Can't find the gym");
		Gym gym = gymOptional.get();
		LocalDateTime todaysMidnigth = LocalDate.now().atTime(LocalTime.MIN);
		List<User> userList = userRepo.findByGymAndDob(gym, todaysMidnigth);
		
		return userList;
	}

	public Map<String, Object> progressDetails(int id) {
		Optional<User> userOptional = userRepo.findById(id);
		Assert.isTrue(userOptional.isPresent(), "Can't find the user");
		User user = userOptional.get();
		
		Progress earliestProgress = progressRepo.findTopByUserOrderByDateTimeAsc(user);
		Progress latestProgress = progressRepo.findTopByUserOrderByDateTimeDesc(user);
		
//		Assert.isTrue(earliestProgress!=null && latestProgress!=null, "No progress found");
		Map<String, Object> map = new HashMap<>();
		
		float idealBMI = 20.5f;
		double bmi = ((user.getWeight()*10000)/(user.getHeigth()*user.getHeigth()));
		map.put("bmi", String.format("%.1f", bmi));
		
		float desiredWeight = idealBMI *(user.getHeigth()*user.getHeigth()) / 10000;
		
		if(earliestProgress==null) {
			map.put("fatMessage", "-");
			map.put("calBurnMessage", "-");
			map.put("waterIntakeMessage", "-");
			map.put("weigthMessage", "-");
			float initialWeight = user.getWeight();
			map.put("initialWeight",initialWeight);
			map.put("desiredWeight", String.format("%.0f", desiredWeight));
			double goalPercentage =
					(desiredWeight - initialWeight)>0?(user.getWeight() - initialWeight) / (desiredWeight - initialWeight):100;
					goalPercentage = goalPercentage > 1 ? 1 : goalPercentage;
					goalPercentage = goalPercentage < 0 ? 0 : goalPercentage;
					goalPercentage = desiredWeight == initialWeight ? 1 : goalPercentage;
			map.put("goalPercentage", String.format("%.1f", goalPercentage));
			String goalMessage = goalPercentage<=0?"":"Wow Great keep it up\n You are "+goalPercentage+"% closer to your Ideal weigth";
			map.put("goalMessage", goalMessage);
			
			return map;
		}
		
		float initialWeight = earliestProgress.getWeight();
		map.put("initialWeight",initialWeight);
		map.put("desiredWeight", String.format("%.0f", desiredWeight));
		
		
		double goalPercentage =
				(desiredWeight - initialWeight)>0?(user.getWeight() - initialWeight) / (desiredWeight - initialWeight):100;
				goalPercentage = goalPercentage > 1 ? 1 : goalPercentage;
				goalPercentage = goalPercentage < 0 ? 0 : goalPercentage;
				goalPercentage = desiredWeight == initialWeight ? 1 : goalPercentage;
				map.put("goalPercentage", String.format("%.1f", goalPercentage));
				String goalMessage = goalPercentage<=0?"":"Wow Great keep it up\n You are "+String.format("%.0f", goalPercentage)+"% closer to your Ideal weigth";
				map.put("goalMessage", goalMessage);
				
		
		
		
		
		try {
			float weigthDiff = earliestProgress.getWeight() - latestProgress.getWeight();			
//			map.put("weigthDiff", String.format("%.2f", weigthDiff));
			map.put("weigthMessage", weigthDiff>=0?""+weigthDiff+"kg gain till now":""+weigthDiff+"kg loss till now");
		}catch (Exception e){
			map.put("weigthMessage", "-");
		}
		try {
			float fatDiff = earliestProgress.getFat() - latestProgress.getFat();
			map.put("fatMessage", fatDiff>=0?""+fatDiff+"% gain till now":""+fatDiff+"% loss till now");
//			map.put("fatDiff", String.format("%.2f", fatDiff));
		}catch (Exception e){
			map.put("fatMessage", "-");
		}
		try {
			float calBurnDiff = earliestProgress.getCalBurn() - latestProgress.getCalBurn();
			map.put("calBurnMessage", calBurnDiff>=0?""+String.format("%.0f", calBurnDiff)+"cal burn incresed":""+String.format("%.0f", calBurnDiff)+"cal burn decreased");
//			map.put("calBurnDiff",String.format("%.2f", calBurnDiff));
		}catch (Exception e){
			map.put("calBurnMessage", "-");
		}
		try {
			float waterIntakeDiff = earliestProgress.getWaterIntake() - latestProgress.getWaterIntake();
//			map.put("waterIntakeDiff", String.format("%.2f", waterIntakeDiff));
			map.put("waterIntakeMessage", waterIntakeDiff>=0?""+waterIntakeDiff+"L increased":""+waterIntakeDiff+"L decreased");
//			map.put("waterIntakeMessage", waterIntakeDiff>=0?""+waterIntakeDiff+"L increased this week":""+waterIntakeDiff+"L decreased this week");
		}catch (Exception e){
			map.put("waterIntakeMessage", "-");
		}
		
//		map.put("bmi",  bmi);
		
		return map;
	}
	
	public String uploadProfileImg(int id, MultipartFile file) {
		Optional<User> userOptional = userRepo.findById(id);
		Assert.isTrue(userOptional.isPresent(), "Can't find the user");
		User user = userOptional.get();
		
		String imagePath = imagePath(file, user);
		
		File f = new File(file.getOriginalFilename());
		try {
			FileOutputStream os = new FileOutputStream(f);
			os.write(file.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		s3Client.putObject(bucketName, imagePath, f);
		
		f.delete();
		
		
		//to store in local disk
//		File f = new File(filePath);
//		if(!f.exists()) {
//			f.mkdir();
//		}
//		System.out.println(imagePath);
//		
//		try {
//			Files.deleteIfExists(Paths.get(imagePath));
//			Files.copy(file.getInputStream(), Paths.get(imagePath));
//		} catch (IOException e) {
//			throw new CustomException("error while uploading image",HttpStatus.NOT_ACCEPTABLE);
//		}
		user.setProfileImg(imagePath);
		userRepo.save(user);
		
		return imagePath;
	}
	
	public String imagePath(MultipartFile file, User user) {
		String fileName = user.getId().toString()+"_"+user.getPhone().toString();
		String imagePath = fileName + file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
		return imagePath;
	}
	
	public HttpServletResponse getProfileImage(String filename, HttpServletResponse response) {
		
		com.amazonaws.services.s3.model.S3Object s3Object = s3Client.getObject(bucketName, filename);
		
		S3ObjectInputStream objectContent = s3Object.getObjectContent();
		
		try {
			StreamUtils.copy(objectContent.getDelegateStream(), response.getOutputStream());
		} catch (IOException e) {
			throw new CustomException("error while getting image",HttpStatus.NOT_ACCEPTABLE);
		}
		return response;
	}
	
	@Scheduled(cron = "${cros.daily}")
	public void updateDailyJob() {

		LocalDateTime currentTime = LocalDateTime.now();
		boolean isFirstDay = currentTime.getDayOfMonth()==1;
		boolean isMonday = currentTime.getDayOfWeek().getValue()==1;
		
		LocalDateTime lastDate = isMonday?currentTime.minusDays(2):currentTime.minusDays(1);
		
		List<User> allUser = userRepo.findByRole(Role.member);
		for(User u: allUser) {
			if(!u.getLastVisit().isAfter(lastDate)) {
				u.setCurrentStreak(0);
				u.setHighestStreakThisMonth(0);
				userRepo.save(u);
			}
			if(isFirstDay) {
				u.setHighestStreak(0);
				userRepo.save(u);
			}
		}
		System.out.println("Updating currentStreak");
	}
	
}
