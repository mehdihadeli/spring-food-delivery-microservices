package com.github.mehdihadeli.buildingblocks.abstractions.problemdetails;

import org.springframework.http.HttpStatus;

public interface IProblemDetailMapper {
    HttpStatus getMappedStatusCodes(Exception exception);
}
