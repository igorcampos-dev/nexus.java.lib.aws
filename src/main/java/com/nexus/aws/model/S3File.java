package com.nexus.aws.model;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class S3File {
    private String filename;
    private long size;

}
