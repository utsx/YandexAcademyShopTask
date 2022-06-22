package ru.yndx.school.shop.entities;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.Objects;

@AllArgsConstructor
@RequiredArgsConstructor
public class Answer {
    @NotNull
    public Integer code;
    @NotNull
    public String message;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Answer answer = (Answer) o;
        return Objects.equals(code, answer.code) && Objects.equals(message, answer.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, message);
    }

    @Override
    public String toString() {
        return "Answer{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}
