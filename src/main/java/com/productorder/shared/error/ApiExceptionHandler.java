package com.productorder.shared.error;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    ProblemDetail notFound(NotFoundException ex) { return problem(HttpStatus.NOT_FOUND, ex.getMessage()); }
    @ExceptionHandler(BusinessRuleException.class)
    ProblemDetail business(BusinessRuleException ex) { return problem(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage()); }
    @ExceptionHandler({MethodArgumentNotValidException.class, IllegalArgumentException.class})
    ProblemDetail badRequest(Exception ex) { return problem(HttpStatus.BAD_REQUEST, "Requisição inválida"); }
    @ExceptionHandler(DataIntegrityViolationException.class)
    ProblemDetail conflict(DataIntegrityViolationException ex) { return problem(HttpStatus.CONFLICT, "Conflito de dados"); }

    private ProblemDetail problem(HttpStatus status, String detail) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(status.getReasonPhrase());
        return problem;
    }
}
