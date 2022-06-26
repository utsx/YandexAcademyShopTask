package ru.yndx.school.shop.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.Objects;

@AllArgsConstructor
@RequiredArgsConstructor
@Schema(description = "Сущность ответа")
public class Answer {
    @NotNull
    @Schema(description = "Код ответа", example = "200")
    public Integer code;
    @NotNull
    @Schema(description = "Сообщение ответа", example = "OK")
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
