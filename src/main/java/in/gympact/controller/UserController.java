package in.gympact.controller;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import in.gympact.entities.Attendance;
import in.gympact.entities.CurrentPackage;
import in.gympact.entities.Diet;
import in.gympact.entities.Message;
import in.gympact.entities.PastWorkout;
import in.gympact.entities.Progress;
import in.gympact.entities.User;
import in.gympact.pojo.DiffProgress;
import in.gympact.pojo.Login;
import in.gympact.pojo.Signup;
import in.gympact.respositry.ExerciseRepositry;
import in.gympact.service.UserService;
import in.gympact.service.WorkoutService;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	UserService userService;
	
	@Autowired
	WorkoutService workoutService;
	
	@Autowired
	ExerciseRepositry exRepo;

//	@CrossOrigin(origins="http://localhost:4200")
	@PostMapping(path="/save")
	public User post(@RequestParam int gymId, @RequestBody User user ) {
		return userService.addUser(gymId,user);
	}
	
	@GetMapping(path="/getUser")
	public User getUser(@RequestParam int id) {
		return userService.getUserById(id);
	}
	
	/*
	 * Login/signup/phone
	 */
	
	@PostMapping(path="/checkPhone")
	public User checkPhone(@RequestBody String phone ) {
		return userService.checkPhone(phone);
	}
	
	@PostMapping(path="/signup")
	public User signup(@RequestBody Signup signup ) {
		return userService.signup(signup);
	}
	
	@PostMapping(path="/login")
	public User login(@RequestBody Login login ) {
		return userService.login(login);
	}
	
	
	
	
	@GetMapping(path="/markAttendance")
	public Attendance markAttendance(@RequestParam int id) {
		return userService.markAttendance(id);
	}
	
	@GetMapping(path="/checkTodaysAttendance")
	public Attendance getAttendance(@RequestParam int id) {
		return userService.getAttendance(id);
	}
	
	@PostMapping(path="/addProgress")
	public String addProgress(@RequestParam int id, @RequestBody Progress progress) {
		return userService.addProgress(id, progress);
	}
	
	@GetMapping(path="/userProgress")
	public List<Progress> getUserProgress(@RequestParam int id){
		return userService.getUserProgress(id);
	}
	
	@GetMapping(path="/progressDetails")
	public Map<String, Object> progressDetails(@RequestParam int id){
		return userService.progressDetails(id);	
	}

	@GetMapping(path="/getDiet")
	public Diet getDiet(@RequestParam int id) {
		return userService.getDiet(id);
	}
	
	@GetMapping(path="/getCurrentPackage")
	public CurrentPackage getCurrentPackage(@RequestParam int id) {
		return userService.getCurrentPackage(id);
	}
	
	@PostMapping(path="/savePastWorkout")
	public String savePastWorkout(@RequestParam int id, @RequestBody PastWorkout pastWorkout) {
		return userService.savePastWorkout(id, pastWorkout);
	}
	
	@GetMapping(path="/getPastWorkouts")
	public List<PastWorkout> getPastWorkouts(@RequestParam int id) {
		return userService.getPastWorkouts(id);
	}
	
	@GetMapping(path="/getNotification")
	public List<Message> getNotification(@RequestParam int id){
		return userService.getNotification(id);	
	}
	
	@GetMapping(path="/search")
	public List<User> searchUser(@RequestParam String query,@RequestParam int gymId){
		return userService.searchUser(query, gymId);	
	}
	
	@GetMapping(path="/highAchieverList")
	public List<DiffProgress> highAchieverList(@RequestParam int gymId){
		return userService.highAchieverList(gymId);	
	}
	
//	@GetMapping(path="/personalBest")
//	public PersonalBest personalBest(@RequestParam int id){
//		return userService.personalBest(id);	
//	}
	
	@GetMapping(path="/homeMessagge")
	public Map<String, List<String>> homeMessagge(@RequestParam int id){
		return userService.homeMessagge(id);	
	}
	
	@GetMapping(path="/birthday")
	public List<User> birthdayList(@RequestParam int gymId){
		return userService.birthdayList(gymId);	
	}
	
	@PostMapping(path="/uploadProfileImg")
	public String uploadProfileImg(@RequestParam int id, @RequestParam MultipartFile img) {
		return userService.uploadProfileImg(id, img);
	}
	
	@GetMapping(path="/getImage")
	public void getImage(@RequestParam String filename, HttpServletResponse response) {
		userService.getProfileImage(filename, response);
	}
	
	
	@GetMapping(path="/postCon")
	public void postCon(){
		userService.postCon();	
	}
	
	@GetMapping(path="/test")
	public String test(){
		return "live!";	
	}
	
	
	

	
		
	
}
