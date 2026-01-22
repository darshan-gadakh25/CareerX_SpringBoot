package com.careerx.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.time.LocalDate;


@MappedSuperclass
@Setter
@Getter
@ToString
@NoArgsConstructor
public class BaseEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@CreationTimestamp
	@Column(name ="created_on")
	private LocalDate createdDate;

	@UpdateTimestamp
	@Column(name = "updated_on")
    private LocalDateTime updatedDate;
}
