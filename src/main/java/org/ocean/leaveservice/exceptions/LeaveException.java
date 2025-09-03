package org.ocean.leaveservice.exceptions;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class LeaveException extends RuntimeException {
    private String error;
    private String message;
    public LeaveException(String error,String message) {
        super(error + " " + message);
        this.error = error;
        this.message = message;
    }
}
