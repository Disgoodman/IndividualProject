package com.example.models.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(name = "uk_user_login", columnNames = "login"))
public class User {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 25, nullable = false)
    @Size(min = 3, max = 25, message = "Login's length should be in range from 3 to 25")
    @NotBlank(message = "Login should not be null, not be empty and not consist of only space characters")
    private String login;

    @Column(nullable = false)
    @NotEmpty(message = "Password can't be null or empty")
    private String password;

    @Column(name = "registration_date", nullable = false)
    private Date registrationDate = Date.from(Instant.now());

    @OneToOne(mappedBy = "user")
    private Profile profile;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "authorities", joinColumns = @JoinColumn(name = "user_id"), foreignKey = @ForeignKey(name = "fk_authority_user"))
    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    public Set<Role> roles;

    public boolean hasProfile() {
        return profile != null;
    }

    public User() {
    }

    public User(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> role) {
        this.roles = role;
    }
}
