package info.agilite.spring.base.handler;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
/**
 * Classe que transforma os erros de validação JSon
 * @author Rafael
 *
 */

@FieldDefaults(makeFinal=true, level=AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
public class Violation {
  String fieldName;
  String message;
}