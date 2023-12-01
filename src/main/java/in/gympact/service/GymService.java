package in.gympact.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import in.gympact.entities.Badges;
import in.gympact.entities.Diet;
import in.gympact.entities.Group;
import in.gympact.entities.Gym;
import in.gympact.entities.Message;
import in.gympact.entities.Package;
import in.gympact.entities.User;
import in.gympact.enums.Enum.Role;
import in.gympact.respositry.BadgesRepositry;
import in.gympact.respositry.DietRepositry;
import in.gympact.respositry.GroupRepositry;
import in.gympact.respositry.GymRepositry;
import in.gympact.respositry.MessageRepositry;
import in.gympact.respositry.PackageRepositry;
import in.gympact.respositry.UserRepositry;

@Service
public class GymService {
	
	@Autowired
	GymRepositry gymRepo;
	
	@Autowired
	DietRepositry dietRepo;
	
	@Autowired
	PackageRepositry packageRepo;
	
	@Autowired
	GroupRepositry groupRepo;
	
	@Autowired
	MessageRepositry messageRepo;
	
	@Autowired
	UserRepositry userRepo;
	
	@Autowired
	BadgesRepositry badgesRepo;

	public Gym saveGym(Gym gym) {
		return gymRepo.save(gym);
	}

	public Gym getGymById(int id) {
		Optional<Gym> gym = gymRepo.findById(id);
		Assert.isTrue(gym.isPresent(), "Can't find the gym");
		return gym.get();
	}

	public String saveDiet(int id, Diet diet) {
		Optional<Gym> gymOptional = gymRepo.findById(id);
		Assert.isTrue(gymOptional.isPresent(), "Can't find the gym");
		
		Gym gym = gymOptional.get();
		
		if(diet.getId()==null){
			List<Diet> dietList = gym.getDiets();
			dietList.add(diet);
			gym.setDiets(dietList);
			diet.setGym(gym);
		}
		dietRepo.save(diet);
		return "Saved";
	}

	public List<Diet> getDietList(int id) {
		Optional<Gym> gymOptional = gymRepo.findById(id);
		Assert.isTrue(gymOptional.isPresent(), "Can't find the gym");
		Gym gym = gymOptional.get();
		return dietRepo.findByGym(gym);
	}
	
	/*
	 * Packages
	 */

	public Package savePackage(int id, Package newPackage) {
		Optional<Gym> gymOptional = gymRepo.findById(id);
		Assert.isTrue(gymOptional.isPresent(), "Can't find the gym");
		Gym gym = gymOptional.get();
		
		List<Package> packageList = gym.getPackages();
		packageList.add(newPackage);
		gym.setPackages(packageList);
		newPackage.setGym(gym);
		
		return packageRepo.save(newPackage);
//		return "Saved";
	}

	public List<Package> getPackageList(int id) {
		Optional<Gym> gymOptional = gymRepo.findById(id);
		Assert.isTrue(gymOptional.isPresent(), "Can't find the gym");
		Gym gym = gymOptional.get();
		return gym.getPackages();
	}
	
	public List<User> getPackageEnded(int id) {
		Optional<Gym> gymOptional = gymRepo.findById(id);
		Assert.isTrue(gymOptional.isPresent(), "Can't find the gym");
		Gym gym = gymOptional.get();
		
		List<User> userList = userRepo.findByGymAndRole(gym, Role.member);
		List<User> userPackageEnded = new LinkedList<>();
		LocalDateTime todaysDate = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT);
		for(User u: userList) {
			if(u.getCurrentPackage() !=null && u.getCurrentPackage().getEndDate().compareTo(todaysDate)<0) {
				userPackageEnded.add(u);
			}
		}
		//remove this
//		List<User> userListTest = userRepo.findAll();
//		return userListTest;
		
		return userPackageEnded;
	}
	
	public List<User> getPackageEndsInDays(int id) {
		int DAYS = 3;
		Optional<Gym> gymOptional = gymRepo.findById(id);
		Assert.isTrue(gymOptional.isPresent(), "Can't find the gym");
		Gym gym = gymOptional.get();
		
		List<User> userList = userRepo.findByGymAndRole(gym, Role.member);
		List<User> userPackageEnds = new LinkedList<>();
		LocalDateTime todaysDate = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT).plusDays(DAYS);
		for(User u: userList) {
			if(u.getCurrentPackage() !=null && u.getCurrentPackage().getEndDate().compareTo(todaysDate)<0) {
				userPackageEnds.add(u);
			}
		}
		//remove this
//		List<User> userListTest = userRepo.findAll();
//		return userListTest;
		return userPackageEnds;
	}
	
	/*
	 * groups
	 */

	public String saveGroup(int id, String groupName) {
		Optional<Gym> gymOptional = gymRepo.findById(id);
		Assert.isTrue(gymOptional.isPresent(), "Can't find the gym");
		Gym gym = gymOptional.get();
		
		Group group = new Group();
		group.setName(groupName);
		group.setTotalMembers(0);
		List<Group> groupList = gym.getGroups();
		groupList.add(group);
		gym.setGroups(groupList);
		group.setGym(gym);
		groupRepo.save(group);
		
		return "Saved";
	}

	public List<Group> getGroups(int id) {
		Optional<Gym> gymOptional = gymRepo.findById(id);
		Assert.isTrue(gymOptional.isPresent(), "Can't find the gym");
		Gym gym = gymOptional.get();
		return gym.getGroups();
	}

	public String addMember(int groupId, int userId) {
		Optional<Group> groupOptional = groupRepo.findById(groupId);
		Assert.isTrue(groupOptional.isPresent(), "Can't find the group");
		Group group = groupOptional.get();
		
		Optional<User> userOptional = userRepo.findById(userId);
		Assert.isTrue(userOptional.isPresent(), "Can't find the user");
		User user = userOptional.get();
		
		List<User> members = group.getMembers();
		members.add(user);
		group.setMembers(members);
		groupRepo.save(group);
		
		return "Added Member";
	}
	
	public String notifyGroup(int groupId, Message message) {
		Optional<Group> groupOptional = groupRepo.findById(groupId);
		Assert.isTrue(groupOptional.isPresent(), "Can't find the group");
		Group group = groupOptional.get();
		
		message.setCreatedOn(LocalDateTime.now());
		message = messageRepo.save(message);
		
		List<Message> messageList= group.getMessages();
		messageList.add(message);
		group.setMessages(messageList);
		
		groupRepo.save(group);
		
		for(User user: group.getMembers()) {
			List<Message> notifications = user.getNotifications();
			notifications.add(message);
			user.setNotifications(notifications);
			userRepo.save(user);
		}
		
		return "Sent";
	}

	public List<Message> getGroupMessages(int groupId) {
		Optional<Group> groupOptional = groupRepo.findById(groupId);
		Assert.isTrue(groupOptional.isPresent(), "Can't find the group");
		Group group = groupOptional.get();
		
		return group.getMessages();
	}

	public List<User> thisMonthStreakList(int id) {
		Optional<Gym> gymOptional = gymRepo.findById(id);
		Assert.isTrue(gymOptional.isPresent(), "Can't find the gym");
		Gym gym = gymOptional.get();
		
		List<User> users = userRepo.findByGymAndRoleOrderByHighestStreakThisMonthDesc(gym, Role.member);
		
		return users;
	}

	public List<Badges> getBadges(int id) {
		Optional<Gym> gymOptional = gymRepo.findById(id);
		Assert.isTrue(gymOptional.isPresent(), "Can't find the gym");
		Gym gym = gymOptional.get();
				
		return badgesRepo.findAll();
	}

	public Map<String, Integer> attendanceDetails(int id) {
		Optional<Gym> gymOptional = gymRepo.findById(id);
		Assert.isTrue(gymOptional.isPresent(), "Can't find the gym");
		Gym gym = gymOptional.get();
		
		LocalDateTime todayMidnight = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT);
		LocalDateTime todayNoon = LocalDateTime.of(LocalDate.now(), LocalTime.NOON);
		
		LocalDateTime yesterdayMidnight = LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.MIDNIGHT);
		LocalDateTime yesterdayNoon = LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.NOON);
		
		
		
		List<User> todayAll = userRepo.findByGymAndRoleAndLastVisitGreaterThanEqual(gym, Role.member, todayMidnight);
		List<User> todayEvening = userRepo.findByGymAndRoleAndLastVisitGreaterThanEqual(gym, Role.member, todayNoon);
		
		List<User> yesterdayAll = userRepo.findByGymAndRoleAndLastVisitGreaterThanEqual(gym, Role.member, yesterdayMidnight);
		List<User> yesterdayEvening = userRepo.findByGymAndRoleAndLastVisitGreaterThanEqual(gym, Role.member, yesterdayNoon);
		
		int todayEve = todayEvening.size();
		int todayMorning = todayAll.size() - todayEve;
		
		int yesterdayMorning = yesterdayAll.size() -yesterdayEvening.size();
		int yesterdayEve = yesterdayEvening.size() - todayAll.size();
		
		Map<String, Integer> result = new HashMap<>();
		//remover this code (only for testing)
//		todayEve=75;
//		todayMorning=85;
//		yesterdayMorning=2;
//		yesterdayEve=2;
		result.put("todayEvening", todayEve);
		result.put("todayMorning", todayMorning);
		result.put("yesterdayMorning", yesterdayMorning);
		result.put("yesterdayEvening", yesterdayEve);

		
		return result;
	}
	

	public Map<String, Object> statistic(int id) {
		Optional<Gym> gymOptional = gymRepo.findById(id);
		Assert.isTrue(gymOptional.isPresent(), "Can't find the gym");
		Gym gym = gymOptional.get();
		
		Map<String, Object> result = new HashMap<>();
		List<User> userList = userRepo.findByGymAndRole(gym, Role.member);
		//LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT) for 00:00
		LocalDateTime thisMonthStartDate = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT).withDayOfMonth(1);
		
		int newJoinee=0;
		int renewed=0;
		int discontinued=0;
		int totalActive=0;
//		int totalMem=0;
		
		Map<Package, Integer> packageCount = new HashMap<>();
		for (User u : userList) {
			if(u.getRole() == (Role.member)) {
				
			
			if (u.getJoinOn().isAfter(thisMonthStartDate)) {
				newJoinee++;

				packageCount.put(u.getCurrentPackage().getPackageDetails(),
						packageCount.containsKey(u.getCurrentPackage().getPackageDetails())
								? packageCount.get(u.getCurrentPackage().getPackageDetails()) + 1
								: 1);
			} else if (u.getCurrentPackage().getStartDate().isAfter(thisMonthStartDate)) {
				renewed++;
				packageCount.put(u.getCurrentPackage().getPackageDetails(),
						packageCount.containsKey(u.getCurrentPackage().getPackageDetails())
						? packageCount.get(u.getCurrentPackage().getPackageDetails()) + 1
								: 1);
			}
			if (u.getCurrentPackage().getEndDate().isAfter(thisMonthStartDate)
					&& u.getCurrentPackage().getEndDate().isBefore(LocalDateTime.now())) {
				discontinued++;
			}
			if (u.getCurrentPackage().getEndDate().isAfter(LocalDateTime.now()))
				totalActive++;
			}
			if(u.getLastVisit().isBefore(LocalDateTime.now())) {
				
			}
		}
		
		result.put("newJoinee", newJoinee);
		result.put("renewed", renewed);
		result.put("discontinued", discontinued);
		result.put("totalActive", totalActive);
		
		Float totalOfTotal = 0f;
		
		List<Map<String, Object>> packageDetails = new LinkedList<>(); 
		for(Map.Entry<Package,Integer> entry : packageCount.entrySet()) {
			Package p = entry.getKey();
			Integer count = entry.getValue();
			Float price = p.getPrice();
			Float total = count*price;
			totalOfTotal+=total;
			
			Map<String, Object> map = new HashMap<>();
			map.put("name", p.getName());
			map.put("members", count);
			map.put("price", price);
			map.put("totalPrice", total);
			packageDetails.add(map);
		}
		
		result.put("packageDetails", packageDetails);
		result.put("totalOfTotal", totalOfTotal);
		

		
		return result;
	}

	public Gym getGymByUserId(int userId) {
		Optional<User> userOptional = userRepo.findById(userId);
		Assert.isTrue(userOptional.isPresent(), "Can't find the user");
		User user = userOptional.get();
		return user.getGym();
	}
	


	
	
	
}
