package in.gympact.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.gympact.entities.Badges;
import in.gympact.entities.Diet;
import in.gympact.entities.Group;
import in.gympact.entities.Gym;
import in.gympact.entities.Message;
import in.gympact.entities.Package;
import in.gympact.entities.User;
import in.gympact.service.GymService;

@RestController
@RequestMapping("/gym")
public class GymController {
	
	@Autowired
	GymService gymService;
	
	@PostMapping(path="/save")
	public Gym saveGym(@RequestBody Gym gym) {
		return gymService.saveGym(gym);
	}

	@GetMapping(path="/getGym")
	public Gym getGym(@RequestParam int id) {
		return gymService.getGymById(id);
	}
	
	@GetMapping(path="/getGymByUserId")
	public Gym getGymByUserId(@RequestParam int userId) {
		return gymService.getGymByUserId(userId);
	}
	
	@PostMapping(path="/saveDiet")
	public String saveDiet(@RequestParam int id, @RequestBody Diet diet) {
		return gymService.saveDiet(id, diet);
	}
	
	@GetMapping(path="/getDiet")
	public List<Diet> getDietList(@RequestParam int id) {
		return gymService.getDietList(id);
	}
	
	/*
	 * Packages
	 */
	
	@PostMapping(path="/savePackage")
	public Package savePackage(@RequestParam int id, @RequestBody Package newPackage) {
		return gymService.savePackage(id, newPackage);
	}
	
	@GetMapping(path="/getPackages")
	public List<Package> getPackageList(@RequestParam int id) {
		return gymService.getPackageList(id);
	}
	
	@GetMapping(path="/getPackageEnded")
	public List<User> getPackageEnded(@RequestParam int id) {
		return gymService.getPackageEnded(id);
	}
	
	@GetMapping(path="/getPackageEndsInDays")
	public List<User> getPackageEndsInDays(@RequestParam int id) {
		return gymService.getPackageEndsInDays(id);
	}
	
	/*
	 * Groups
	 */
	
	@GetMapping("/saveGroup")
	public String saveGroup(@RequestParam int id, @RequestParam String groupName) {
		return gymService.saveGroup(id, groupName);
	}
	
	@GetMapping(path="/getGroups")
	public List<Group> getGroups(@RequestParam int id) {
		return gymService.getGroups(id);
	}
	
	@GetMapping(path="/addMember")
	public String addMember(@RequestParam int groupId, @RequestParam int userId) {
		return gymService.addMember(groupId, userId);
	}
	
	@PostMapping("/notifyGroup")
	public  String notifyGroup(@RequestParam int groupId, @RequestBody Message message) {
		return gymService.notifyGroup(groupId, message);
	}
	
	@GetMapping(path="/getGroupMessages")
	public List<Message> getGroupMessages(@RequestParam int groupId){
		return gymService.getGroupMessages(groupId);	
	}
	
	/*
	 * Streak
	 */
	
	@GetMapping(path="/thisMonthStreakList")
	public List<User> thisMonthStreakList(@RequestParam int id){
		return gymService.thisMonthStreakList(id);	
	}
	
	
	
	//Badges
	
	@GetMapping(path="/getBadges")
	public List<Badges> getBadges(@RequestParam int id){
		return gymService.getBadges(id);	
	}
	
	//details
	@GetMapping(path="/statistic")
	public Map<String, Object> statistic(@RequestParam int id){
		return gymService.statistic(id);	
	}
	
	@GetMapping(path="/attendanceDetails")
	public Map<String, Integer> attendanceDetails(@RequestParam int id){
		return gymService.attendanceDetails(id);	
	}
	

	
	
}
