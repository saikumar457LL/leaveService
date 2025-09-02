package org.ocean.leaveservice.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FieldValidationError {
    private String field;
    private String message;
    private String rejectedValue;
}
