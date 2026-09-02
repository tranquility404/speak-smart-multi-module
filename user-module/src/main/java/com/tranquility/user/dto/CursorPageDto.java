package com.tranquility.user.dto;

import java.util.List;

public record CursorPageDto<T>(
        List<T> data,
        String nextCursor,
        boolean hasNext
) { }