package com.nexus.aws.model;

import lombok.Data;
import org.springframework.stereotype.Component;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Data
@Component
public class FileEmpty {
    public static InputStream inputStream = new ByteArrayInputStream(new byte[0]);
}
