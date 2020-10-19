package tasks.basics

import scala.collection.immutable.ArraySeq
import scala.collection.mutable

object implicits {

  object SuperVipCollections4s {
    type SizeScore = Int

    trait GetSizeScore[T] {
      def apply(value: T): SizeScore
    }

    object syntax {

      implicit class GetSizeScoreOps[T: GetSizeScore](inner: T) {
        def sizeScore: SizeScore = implicitly[GetSizeScore[T]].apply(inner)
      }
    }

    final class MutableBoundedCache[K: GetSizeScore, V: GetSizeScore](maxSizeScore: SizeScore) {

      import syntax._
      import instances._

      private val map = mutable.LinkedHashMap.empty[K, V]

      def put(key: K, value: V): Unit = {

        if (map.sizeScore + key.sizeScore + value.sizeScore > maxSizeScore) {
          if (map.nonEmpty) {
            map -= map.head._1
            put(key, value)
          }
        } else { map.put(key, value) }
      }

      def get(key: K): Option[V] = map.get(key)
    }

    final case class PackedMultiMap[K, +V](inner: ArraySeq[(K, V)])

    object PackedMultiMap {
      def empty[K, V]: PackedMultiMap[K, V] = PackedMultiMap()

      def apply[K, V](values: (K, V)*): PackedMultiMap[K, V] =
        PackedMultiMap(inner = ArraySeq(values: _*))
    }

    trait Iterate[-F[_]] {
      def iterator[T](f: F[T]): Iterator[T]
    }

    trait Iterate2[-F[_, _]] {
      def iterator1[T, S](f: F[T, S]): Iterator[T]
      def iterator2[T, S](f: F[T, S]): Iterator[S]
    }

    object instances {
      import syntax._

      implicit val iterableOnceIterate: Iterate[Iterable] = new Iterate[Iterable] {
        override def iterator[T](f: Iterable[T]): Iterator[T] = f.iterator
      }

      implicit val arrayIterate: Iterate[Array] = new Iterate[Array] {
        override def iterator[T](f: Array[T]): Iterator[T] = f.iterator
      }

      implicit val mapIterate: Iterate2[Map] = new Iterate2[Map] {
        override def iterator1[K, V](f: Map[K, V]): Iterator[K] = f.keys.iterator
        override def iterator2[K, V](f: Map[K, V]): Iterator[V] = f.values.iterator
      }

      implicit val packedMultiMapIterate: Iterate2[PackedMultiMap] = new Iterate2[PackedMultiMap] {

        override def iterator1[K, V](f: PackedMultiMap[K, V]): Iterator[K] =
          f.inner.map { case (k, _) => k }.iterator

        override def iterator2[K, V](f: PackedMultiMap[K, V]): Iterator[V] =
          f.inner.map { case (_, v) => v }.iterator
      }

      implicit val byteScore: GetSizeScore[Byte] = _ => 1
      implicit val charScore: GetSizeScore[Char] = _ => 2
      implicit val intScore: GetSizeScore[Int] = _ => 4
      implicit val longScore: GetSizeScore[Long] = _ => 8
      implicit val stringScore: GetSizeScore[String] = string => 12 + 2 * string.length

      implicit def listSizeScore[T: GetSizeScore]: GetSizeScore[List[T]] =
        (value: List[T]) => {
          value.foldLeft(12)((acc, e) => acc + e.sizeScore)
        }

      implicit def vectorSizeScore[T: GetSizeScore]: GetSizeScore[Vector[T]] =
        (value: Vector[T]) => {
          value.foldLeft(12)((acc, e) => acc + e.sizeScore)
        }

      implicit def arraySizeScore[T: GetSizeScore]: GetSizeScore[Array[T]] =
        (value: Array[T]) => {
          value.foldLeft(12)((acc, e) => acc + e.sizeScore)
        }

      implicit def packedMultiMapScore[K: GetSizeScore, V: GetSizeScore]
        : GetSizeScore[PackedMultiMap[K, V]] =
        (value: PackedMultiMap[K, V]) => {
          value.inner.foldLeft(12)((acc, m) => acc + m._1.sizeScore + m._2.sizeScore)
        }

      implicit def mapSizeScore[K: GetSizeScore, V: GetSizeScore]: GetSizeScore[Map[K, V]] =
        (value: Map[K, V]) => {
          value.foldLeft(12)((acc, m) => acc + m._1.sizeScore + m._2.sizeScore)
        }

      implicit def linkedHashMapSizeScore[K: GetSizeScore, V: GetSizeScore]
        : GetSizeScore[mutable.LinkedHashMap[K, V]] =
        (value: mutable.LinkedHashMap[K, V]) => {
          value.foldLeft(0)((acc, m) => acc + m._1.sizeScore + m._2.sizeScore)
        }

    }
  }

  object MyTwitter {
    import SuperVipCollections4s._
    import syntax._
    import instances._

    final case class Twit(
      id: Long,
      userId: Int,
      hashTags: Vector[String],
      attributes: PackedMultiMap[String, String],
      fbiNotes: List[FbiNote]
    )

    final case class FbiNote(
      month: String,
      favouriteChar: Char,
      watchedPewDiePieTimes: Long
    )

    trait TwitCache {
      def put(twit: Twit): Unit
      def get(id: Long): Option[Twit]
    }

    implicit def twitSizeScore: GetSizeScore[Twit] =
      (x: Twit) =>
        x.id.sizeScore + x.userId.sizeScore + x.hashTags.sizeScore + x.attributes.sizeScore + x.fbiNotes.sizeScore

    implicit def fbiNoteSizeScore: GetSizeScore[FbiNote] =
      (x: FbiNote) =>
        x.month.sizeScore + x.favouriteChar.sizeScore + x.watchedPewDiePieTimes.sizeScore

    def createTwitCache(maxSizeScore: SizeScore): TwitCache =
      new TwitCache {

        val map = new MutableBoundedCache[Long, Twit](maxSizeScore)
        override def put(twit: Twit): Unit = map.put(twit.id, twit)
        override def get(id: Long): Option[Twit] = map.get(id)

      }
  }
}
