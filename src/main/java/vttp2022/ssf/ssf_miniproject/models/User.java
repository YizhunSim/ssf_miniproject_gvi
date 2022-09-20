package vttp2022.ssf.ssf_miniproject.models;

import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Reference;
import org.springframework.data.annotation.Transient;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

//@JsonIgnoreProperties only allowing setters (so that we can load the data) but hiding the getters during serialization
@JsonIgnoreProperties(value = {"password", "passwordConfirm" }, allowSetters = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Data
@RedisHash
public class User {

  @Id
  @ToString.Include
  private String id;

  // @NotNull
  // @Size(min = 2, max = 48)
  // @ToString.Include
  // private String name;

  @NotNull
  @Email
  @EqualsAndHashCode.Include
  @ToString.Include
  @Indexed
  private String email;

  @NotNull
  private String password;

  @Transient
  private String passwordConfirm;

  @NotNull
  @Size(min = 2, max = 48)
  @ToString.Include
  @JsonProperty("first_name")
  private String firstName;

  @NotNull
  @Size(min = 2, max = 48)
  @ToString.Include
  @JsonProperty("last_name")
  private String lastName;

  @NotNull
  private boolean enabled;

  @NotNull
  private String photos;

  @Reference
  private Set<Role> roles = new HashSet<Role>();

  public void addRole(Role role) {
    roles.add(role);
  }

  @Transient
  public String getPhotosImagePath(){
      if (id == null || photos == null) return "/images/default-user.png";

      return "/user-photos/" + this.id + "/" + this.photos;
  }

}
