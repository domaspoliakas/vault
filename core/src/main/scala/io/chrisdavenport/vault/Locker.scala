package io.chrisdavenport.vault

import cats.implicits._
import io.chrisdavenport.unique.Unique

/**
 * Locker - A persistent store for a single value.
 * This utilizes the fact that a unique is linked to a type.
 * Since the key is linked to a type, then we can cast the
 * value to Any, and join it to the Unique. Then if we
 * are then asked to unlock this locker with the same unique, we
 * know that the type MUST be the type of the Key, so we can
 * bring it back as that type safely.
 **/
final class Locker private(private val unique: Unique, private val a: Any){
  /**
   * Retrieve the value from the Locker. If the reference equality
   * instance backed by a `Unique` value is the same then allows
   * conversion to that type, otherwise as it does not match
   * then this will be `None`
   * 
   * @param k The key to check, if the internal Unique value matches
   * then this Locker can be unlocked as the specifed value
   */
  def unlock[A](k: Key[A]): Option[A] = Locker.unlock(k, this)
}

object Locker {
  /**
   * Put a single value into a Locker
   */
  def lock[A](k: Key[A], a: A): Locker = new Locker(k.unique, a.asInstanceOf[Any])

  /**
   * Retrieve the value from the Locker. If the reference equality
   * instance backed by a `Unique` value is the same then allows
   * conversion to that type, otherwise as it does not match
   * then this will be `None`
   * 
   * @param k The key to check, if the internal Unique value matches
   * then this Locker can be unlocked as the specifed value
   * @param l The locked to check against
   */
  def unlock[A](k: Key[A], l: Locker): Option[A] = 
    // Equality By Reference Equality
    if (k.unique === l.unique) Some(l.a.asInstanceOf[A])
    else None
}