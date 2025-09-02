package org.ocean.leaveservice.responses;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ErrorResponse {
    private String path;
    private String error;
    private List<FieldValidationError> fieldErrors;
}
