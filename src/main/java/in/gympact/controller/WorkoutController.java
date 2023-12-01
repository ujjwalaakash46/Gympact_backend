package in.gympact.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.gympact.entities.Workout;
import in.gympact.service.WorkoutService;

@RestController
@RequestMapping("/workout")
public class WorkoutController {
	
	@Autowired
	WorkoutService workoutService;

	@PostMapping(path="/")
	public Workout saveWorkout(@RequestParam int userId,@RequestBody Workout workout) {
		return workoutService.addWorkout(userId, workout);
	}
	
	@GetMapping(path="/")
	public Workout getWorkoutById(@RequestParam int id) {
		return workoutService.getWorkoutById(id);
	}
	
	@GetMapping(path="/list")
	public List<Workout> getWorkoutByUser(@RequestParam int userId) {
		return workoutService.getWorkoutByUser(userId);
	}
	
	@GetMapping(path="/listForGym")
	public List<Workout> getWorkoutForGym(@RequestParam int gymId) {
		return workoutService.getWorkoutForGym(gymId);
	}
}
