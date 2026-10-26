package com.bober.todoapp.domain.dto;

import com.bober.todoapp.domain.entity.TaskPriority;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

public record CreateTaskRequestDTO(
        @NotBlank(message = ERROR_MESSAGE_TITLE_LENGTH)
        @Length(max = 255, message = ERROR_MESSAGE_TITLE_LENGTH)
        String title,
        @Nullable
        @Length(max = 1000, message = ERROR_MESSAGE_DESCRIPTION_LENGTH)
        String description,
        @Nullable
        @FutureOrPresent(message = ERROR_MESSAGE_DUE_DATE)
        LocalDate dueDate,
        @NotNull(message = ERROR_MESSAGE_PRIORITY)
        TaskPriority priority

) {
    private static final String ERROR_MESSAGE_TITLE_LENGTH = "Title must be between 1 and 255 characters";
    private static final String ERROR_MESSAGE_DESCRIPTION_LENGTH = "Description must be less than 255 characters";
    private static final String ERROR_MESSAGE_DUE_DATE = "Date must be in future or present";
    private static final String ERROR_MESSAGE_PRIORITY = "Task priority must be selected";
}
