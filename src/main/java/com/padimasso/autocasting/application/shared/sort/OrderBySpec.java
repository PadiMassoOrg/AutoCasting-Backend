package com.padimasso.autocasting.application.shared.sort;

import org.springframework.data.domain.Sort;

public interface OrderBySpec {
    Sort toSort();
}
