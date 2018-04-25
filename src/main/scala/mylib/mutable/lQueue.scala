package mylib
package mutable

import mylib.modifications._
import mylib.contracts._
import mylib.exceptions._
import scala.reflect.ClassTag

/**
 * Implementação duplamente encadeada de de Queue(fila).
 *
 * @author Rafael G. de Paulo
 */ 
case class LQueue[T](values: T*) extends Queue[T]
with DLinked[T, LQueueNode[T]]
with Foreach[T]
with Reduce[T]
with Filter[T]
with Map[T]
{
  private var _size: Int = 0											// o tamanho da fila
  private var _head: Option[LQueueNode[T]] = None	// referência a cabeça da fila
  private var _tail: Option[LQueueNode[T]] = None	// referência a cauda da fila

  /** única parte do Construtor que faz alguma coisa **********************************************************************************************/
  values foreach { this.push(_) } // colocando todos os argumentos para a LQueue
  /** única parte do Construtor que faz alguma coisa **********************************************************************************************/

  def instantiate[A: ClassTag](inc: Int): LQueue[A] = new LQueue[A]
  def size = _size																// retorna o tamanho da fila
  def isEmpty = size == 0
  def push(value: T) {                            // coloca um valor em um elemento, e adiciona ele à cauda da fila
    _tail match {
      case Some(node) => {                        // fila não-vazia
        node.prev = Some(LQueueNode[T](value, None, _tail))
        _tail = node.prev
      }
      case None => {                              // fila vazia
        _head = Some(LQueueNode[T](value))
        _tail = _head 
      }
    }
    _size += 1
  }
  def pop(): Option[T] = _head match {            // retira um elemento da fila, se possível, e retorna o seu valor interno
    case None       => None                       // fila vazia
    case Some(node) => {                          // fila não vazia
      _head = node.prev
      _head match { case Some(anode) => anode.next = None case None => }
      _size -= 1
      Some(node.value)
    }
  }
  def head: Option[T] = _head match {							// retorna o valor do elemento da cabeça, se existe
		case Some(v) => Some(v.value)
		case None => None
	}
  def tail: Option[T] = _tail match {							// retorna o valor do elemento da cauda, se existe
		case Some(v) => Some(v.value)
		case None => None
	}
  
  def next(node: LQueueNode[T])        = node.prev  // retorna o próximo node
  def prev(node: LQueueNode[T])        = node.next  // retorna o node anterior
  def firstNode: Option[LQueueNode[T]] = _head      // retorna a cabeça da fila (assim foreach itera nela da cabeça a cauda)
  def lastNode:  Option[LQueueNode[T]] = _tail      // retorna a cauda da fila

  override def getIterator(ind: Int): LQueueIterator[T] = {
    
    def getCorrectNode(i: Int, node: LQueueNode[T], fwd: Boolean): LQueueNode[T] = 
    (i compare 0) match {
      case -1 => throw new NegativeIndex("LQueue Iterator inicializado com indice negativo") // placeholder
      case  0 => node
      case  1 => (if (fwd) node.prev else node.next) match {
        case Some(nextNode) => getCorrectNode(i - 1, nextNode, fwd)
        case None => {
          throw new ShouldntExecute("LQueue.getIterator() poorly implemented")    // não deve nunca acontecer
        }
      }
    }

    if (ind >= 0) {
      _head match {
        case None       => throw new EmptyEDIterator("tentando criar Iterator para um LQueue vazio")
        case Some(node) => LQueueIterator[T](this, getCorrectNode(ind % size, node, true))
      }
    }
    else {
      _tail match {
        case None       => throw new EmptyEDIterator("tentando criar Iterator para um LQueue vazio")
        case Some(node) => LQueueIterator[T](this, getCorrectNode((-ind -1) % size, node, false))
      }
    }
    
  }
}

case class LQueueNode[T](
  val _value: T,                              		// o valor do Node
  var   prev: Option[LQueueNode[T]] = None,				// referência ao Node anterior (mais próximo da cauda)
  var   next: Option[LQueueNode[T]] = None				// referência ao próximo Node  (mais próximo da cabeça)
) extends DNode[T, LQueueNode[T]]

case class LQueueIterator[T](
  private val queue: LQueue[T],
  private var node:  LQueueNode[T]
) extends DIterator[T](queue) {
  def value: T = node.value
  def hasPrev: Boolean = node.next match {
    case Some(aNode) => true
    case None        => false
  }
  def hasNext: Boolean = node.prev match {
    case Some(aNode) => true
    case None        => false
  }
  def prev() {
    (node.next) match {
      case Some(prevNode) => node = prevNode
      case None           => throw new IteratorOutOfBounds("tentando dar prev() em um iterator no primeiro elemento")
    }
  }
  def next() {
    (node.prev) match {
      case Some(prevNode) => node = prevNode;
      case None           => throw new IteratorOutOfBounds("tentando dar next() em um iterator no primeiro elemento")
    }
  }
}