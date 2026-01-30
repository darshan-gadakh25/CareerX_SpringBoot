package com.careerx.entities;

import com.careerx.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@AttributeOverride(name = "id", column = @Column(name = "user_id"))
@ToString(callSuper = true)
@Builder
public class Users extends BaseEntity {

	@NotBlank(message = "Name is required")
	@Column(nullable = false)
	private String name;

	@JsonIgnore
	@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{6,}$", message = "Password must contain at least one letter, one number, and one special character")
	private String password;

	@NotBlank
	@Email(message = "Invalid Email Address")
	@Column(nullable = false, unique = true)
	private String email;

	@Min(value = 1, message = "Age must be between 1 and 100")
	@Max(value = 100, message = "Age must be between 1 and 100")
	@Column(nullable = false)
	private int age;

	@Builder.Default
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private UserRole role = UserRole.STUDENT;

	@NotBlank(message = "Location is required")
	@Column(nullable = false)
	private String location;

	@Column(name = "profile_picture_url")
	private String profilePictureUrl;

	// Future relationship example
	// @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval =
	// true)
	// private List<StudentProfile> studentProfiles;
}