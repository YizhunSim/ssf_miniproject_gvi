package vttp2022.ssf.ssf_miniproject.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@RedisHash
public class Role {
  @Id
  private String id;

  // @Indexed annotation can be used to create a secondary index. Secondary indexes enable lookup operations based on native Redis structures
  @Indexed
  private String name;

  private String description;

  @Override
	public String toString() {
		return this.name;
	}

}
