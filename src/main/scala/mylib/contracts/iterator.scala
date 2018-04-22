package mylib
import mylib.contracts._

/**
 *  Classe abstrata que define o contrato
 * de um iterator de uma Estrutura de Dados linear.
 *
 *  Um Iterator pode ser usado para percorrer uma
 * Estrutura Linear, indo de um elemento para o próximo,
 * recebendo os dados deles
 *
 * @author Rafael G. de Paulo
 */
abstract class Iterator[T](private val estLin: EstLin[T]) {
  def value: T                            // pega o valor atual

  def hasNext: Boolean                    // checa se tem um próximo elemento
  def next: Option[Iterator[T]]           // pega o próximo elemento
}