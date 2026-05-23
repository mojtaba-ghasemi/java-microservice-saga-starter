package io.github.mojtaba.microservice.starter.iam.servicemodel.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PageRequestDto {
    private int pageSize;
    private int pageNumber;
    private String sortBy;
    private Sort.Direction direction;
}
