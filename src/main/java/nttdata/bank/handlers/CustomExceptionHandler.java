package nttdata.bank.handlers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import nttdata.bank.exceptions.ExceptionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler({ContaException.class})
    public ResponseEntity<ExceptionResponse> handleContaException(
            final Exception e, final HttpServletRequest request) {
        ContaException contaException = (ContaException) e;
        ExceptionResponse exceptionResponse =  ExceptionResponse.of(contaException.getCode(),
                e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(contaException.getCode()).body(exceptionResponse);
    }

    @ExceptionHandler({TransacaoException.class})
    public ResponseEntity<ExceptionResponse> handleTransacaoException(
            final Exception e, final HttpServletRequest request) {
        TransacaoException transacaoException = (TransacaoException) e;
        ExceptionResponse exceptionResponse =  ExceptionResponse.of(transacaoException.getCode(),
                e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(transacaoException.getCode()).body(exceptionResponse);
    }

    @ExceptionHandler({UsuarioException.class})
    public ResponseEntity<ExceptionResponse> handleUsuarioException(
            final Exception e, final HttpServletRequest request) {
        UsuarioException usuarioException = (UsuarioException) e;
        ExceptionResponse exceptionResponse =  ExceptionResponse.of(usuarioException.getCode(),
                e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(usuarioException.getCode()).body(exceptionResponse);
    }

    @ExceptionHandler({AutenticacaoException.class})
    public ResponseEntity<ExceptionResponse> handleAutenticacaoException(
            final Exception e, final HttpServletRequest request) {
        AutenticacaoException autenticacaoException = (AutenticacaoException) e;
        ExceptionResponse exceptionResponse =  ExceptionResponse.of(autenticacaoException.getCode(),
                e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(autenticacaoException.getCode()).body(exceptionResponse);
    }

    @ExceptionHandler({ArquivoException.class})
    public ResponseEntity<ExceptionResponse> handleArquivoException(
            final Exception e, final HttpServletRequest request) {
        ArquivoException arquivoException = (ArquivoException) e;
        ExceptionResponse exceptionResponse =  ExceptionResponse.of(arquivoException.getCode(),
                e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(arquivoException.getCode()).body(exceptionResponse);
    }
}
