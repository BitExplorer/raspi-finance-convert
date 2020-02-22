package finance.services

import org.springframework.stereotype.Service


@Service
class Example {

  def arrayExample() : Unit = {
    val favNums = new Array[Int](20)
    val friends = Array("bob", "tom")

    friends(0) = "sue"
    println(friends(0))
  }
}
