package com.example.petitionplatform.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class ThresholdRequest {
    @Min(1)
    private int threshold;

    public int getThreshold() {
        return threshold;
    }
    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }
}
