package com.fyp.reconciliation_automation.AwsBucket;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AwsBucketResponse {
    private boolean status;
    private String message;
    private Object data;

}
