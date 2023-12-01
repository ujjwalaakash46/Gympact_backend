package in.gympact.service;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import in.gympact.entities.Exercise;
import in.gympact.entities.Gym;
import in.gympact.entities.User;
import in.gympact.entities.Workout;
import in.gympact.enums.Enum.Role;
import in.gympact.respositry.ExerciseRepositry;
import in.gympact.respositry.GymRepositry;
import in.gympact.respositry.UserRepositry;
import in.gympact.respositry.WorkoutRepositry;

@Service
public class WorkoutService {
	
	@Autowired
	WorkoutRepositry workoutRepo;
	
	@Autowired
	UserRepositry userRepo;
	
	@Autowired
	GymRepositry gymRepo;
	
	@Autowired
	ExerciseRepositry exRepo;
	
	
	public Workout addWorkout(int userId, Workout workout) {

		Optional<User> userOptional = userRepo.findById(userId);
		
		Assert.isTrue(userOptional.isPresent(), "Can't find the user who is add this");
		User user  = userOptional.get();
		
		List<Exercise> newExercises = new LinkedList<>();
		for(Exercise ex : workout.getExercises()) {
			newExercises.add(exRepo.save(ex));
		}
		workout.setExercises(newExercises);
		
		workout.setUserRole(user.getRole());
		workout.setGym(user.getGym());
		workout.setCreatedDate(LocalDateTime.now());
		workout.setUpdatedDate(LocalDateTime.now());
		workoutRepo.save(workout);
		if(user.getRole()==Role.member) {
			List<Workout> workoutList = user.getWorkoutList();
			workoutList.add(workout);
			user.setWorkoutList(workoutList);
			userRepo.save(user);
		}
		return workout;
	}
	
	public Workout getWorkoutById(int id) {
		Optional<Workout> workout = workoutRepo.findById(id);
		Assert.isTrue(workout.isPresent(), "Can't find the routine");
		return workout.get();

	}
	
	
	@PostMapping(path="/addEx")
	public Exercise addEx(@RequestBody Exercise ex ) {
		return exRepo.save(ex);
	}
	
	@GetMapping(path="/getEx")
	public Exercise get(@RequestParam int id) {
		Optional<Exercise> ex = exRepo.findById(id);
		Assert.isTrue(ex.isPresent(), "Can't find the exercise");
		return ex.get();

	}

	public List<Workout> getWorkoutByUser(int userId) {
		Optional<User> userOptional = userRepo.findById(userId);
		Assert.isTrue(userOptional.isPresent(), "Can't find the user");
		User user = userOptional.get();
		
		return user.getWorkoutList();
	}

	public List<Workout> getWorkoutForGym(int gymId) {
		Optional<Gym> gymOptional = gymRepo.findById(gymId);
		Assert.isTrue(gymOptional.isPresent(), "Can't find the gym");
		
		List<Workout> workoutList = workoutRepo.findByGymAndUserRoleIsNot(gymOptional.get(), Role.member);
		return workoutList;
	}
}
