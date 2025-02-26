package com.example.database.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
     int id;
    @Column(name = "password")
     String password;
    @Column(name = "full_name")
     String fullName;
    @Column(name = "email")
     String email;
    @Column(name = "dob")
     LocalDate dob;
     Set<String> roles;
    String resetToken;
}
