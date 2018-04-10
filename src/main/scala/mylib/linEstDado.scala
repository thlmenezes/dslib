package mylib

/*
 *  Trait que define o contrato de uma
 * Estrutura de Dados linear encadeada.
 * (Rafael G. de Paulo)
 *
*/
trait LinEstDado[T] {
    def head: Option[T]
    def tail: Option[T]
    def size: Int
}