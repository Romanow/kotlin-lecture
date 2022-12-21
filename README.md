# Kotlin Language

## Базовый синтаксис

```kotlin
private var commonSurnames = listOf("Morozov", "Ivanov", "Sidorov")

open class Person(var name: String, var surname: String, private val gender: String) {
    constructor(name: String, surname: String) : this(name, surname, "Male")

    val male: Boolean
        get() = gender.lowercase() === "male"

    override fun toString(): String {
        return "Person(firstName='$name', lastName='$surname', gender='$gender')"
    }
}

class Male(name: String, surname: String) : Person(name, surname)

class Female(name: String, surname: String) : Person(name, surname, "female")

fun canChangeSurname(person: Person) =
    if (!person.male) {
        commonSurnames.any { person.surname.startsWith(it) }
    } else {
        false
    }

fun main() {
    val male = Male("Alexey", "Romanov")
    val female = Female("Kate", "Morozova")
    val child = Person("Polina", "Romanova", "Female")

    println(male.surname in commonSurnames)
    println(canChangeSurname(female))

    female.surname = "Romanova"

    for (i in 100 downTo 10 step 10) {
        println(i)
    }

    val temp = 10
    val result = when (temp) {
        in 0..15 -> "cold"
        in 16..22 -> "comfort"
        else -> "uncomfortable"
    }
    println(result)

    val text = """
        Скажи-ка дядя, ведь недаром
        Москву спалили перегаром
    """.trimIndent()

    println(text)
}
```

### `data class`

```kotlin
data class Pair<L, R>(
    var left: L,
    var right: R
)

fun main() {
    val pair = Pair(10, "Hello")
    println(pair)
    val (left, right) = pair
    println("Left: $left, right: $right")
}
```

### Properties

```kotlin
enum class TemperatureType { CELSIUS, FAHRENHEIT }

class Temperature(
    private val degrees: Double,
    private val type: TemperatureType,
) {
    val celsius: Double
        get() {
            return if (type == TemperatureType.CELSIUS) {
                degrees
            } else {
                (degrees - 32.0) * 5.0 / 9.0
            }
        }

    val fahrenheit: Double
        get() {
            return if (type == TemperatureType.FAHRENHEIT) {
                degrees
            } else {
                degrees * 9.0 / 5.0 + 32.0
            }
        }
}

fun main() {
    val temperature = Temperature(10.0, TemperatureType.CELSIUS)
    println(temperature.celsius)
    println(temperature.fahrenheit)
}
```

### Getter and Setter

```kotlin
private enum class StorageType { HDD, SSD, TAPE }

class DataStorage {
    private var storage: StorageType = StorageType.HDD

    var storageType: String
        get() = storage.name
        set(value) {
            if (storageType !in StorageType.values().map { it.name }) {
                throw IllegalArgumentException("$value not in ${StorageType.values()}")
            }
            storage = StorageType.valueOf(value)
        }
}

fun main() {
    val dataStorage = DataStorage()
    println(dataStorage.storageType)
    dataStorage.storageType = "SSD"
    println(dataStorage.storageType)
}
```

## Scope functions

Kotlin содержит несколько функций, единственной целью которых является выполнение блока кода в контексте объекта. Эти
функции формируют временную область видимости для объекта, к которому были применены, и вызывают код, указанный в
переданном лямбда-выражении. В этой области видимости можно получить доступ к объекту без явного к нему обращения по
имени. Такие функции называются функциями области видимости (англ. scope functions). Всего их пять: `let`,
`run`, `with`, `apply`, и `also`.

#### let

Контекстный объект доступен в качестве аргумента (`it`).

Возвращаемое значение - результат выполнения лямбды.

Если значение переменной вычислялось при помощи цепочки операций, то `let` позволяет использовать полученный результат
для вызова одной или нескольких функций в блоке кода.

`let` часто используется для выполнения блока кода только с not `null` значениями: `?.let { }`.

#### with

Не является функцией-расширением. Контекстный объект передается в качестве аргумента, а внутри лямбда-выражения он
доступен как получатель (`this`).

Возвращаемое значение - результат выполнения лямбды.

Функцию `with` рекомендуется использовать для вызова функций контекстного объекта без предоставления результата лямбды.
В коде with может читаться как "_с этим объектом, сделайте следующее_"

#### run

Контекстный объект доступен в качестве получателя (`this`).

Возвращаемое значение - результат выполнения лямбды.

`run` делает то же самое, что и `with`, но вызывается как `let` - как функция расширения контекстного объекта. `run`
удобен, когда лямбда содержит и инициализацию объекта, и вычисление возвращаемого значения.

#### apply

Контекстный объект доступен в качестве получателя (`this`).

Возвращаемое значение - контекстный объект.

`apply` используется для такого блока кода, который не возвращает значение и в основном работает с членами
объекта-получателя. Типичный способ использования функции `apply` - настройка объекта-получателя.

#### also

Контекстный объект доступен в качестве аргумента (`it`).

Возвращаемое значение - контекстный объект.

`also` подходит для выполнения таких действий, которые принимают контекстный объект в качестве аргумента. То есть, эту
функции следует использовать, когда требуется ссылка именно на объект, а не на его свойства и функции. Либо, когда
требуется чтобы была доступна ссылка на `this` из внешней области видимости.

#### резюме

Краткое руководство по выбору функции области видимости в зависимости от предполагаемого назначения:

* Выполнение лямбды для not `null` объектов: `let`.
* Представление переменной в виде выражения со своей локальной областью видимости: `let`.
* Настройка объекта: `apply`.
* Настройка объекта и вычисление результата: `run`.
* Выполнение операций, для которых требуется выражение: `run` без расширения.
* Применение дополнительных значений: `also`.
* Группировка всех функций, вызываемых для объекта: `with`.

## Nullability and Elvis Operator

```kotlin
fun main() {
    val a: String = "Hello, world"
    println(a.lowercase())
    // a = null

    val b: String? = null
    println(b?.lowercase())
    val c: String? = b ?: "empty"
    println(c)

    println(c!!.lowercase())
}
```

```kotlin
fun lower(s: String?) = s?.lowercase() ?: "empty"

fun main() {
    lower(null) // empty
    lower("ABC") // abc
}
```

## Extensions

```kotlin
fun String.quoted() = "'$this'"
fun String.wrap(tag: String) = "<$tag>$this</$tag>"

fun String?.isNullOrEmpty() = this == null || isEmpty()

fun main() {
    println("cat".quoted()) // 'cat'
    println("cat".wrap("animal")) // <animal>cat</animal>
}
```

## ️Named Arguments

```kotlin
fun color(red: Int = 0, green: Int = 0, blue: Int = 0) = "RGB($red, $green, $blue)"

fun main() {
    println(color(139)) // RGB(139, 0, 0)
    println(color(blue = 139)) // RGB(0, 0, 139)
    println(color(255, 165)) // RGB(255, 165, 0)
    println(color(red = 128, blue = 128)) // RGB(128, 0, 128)
}
```

### High order functions

```kotlin
fun <T> List<T>.log(filter: (T) -> Boolean, action: (index: Int, item: T) -> Unit) {
    this.filter(filter).forEachIndexed(action)
}

fun main() {
    val items = List(10) { it * it }
    items.log({ it > 0 }, { index, item -> println("$index: $item") })
}
```

```kotlin
fun <A, B, C> ((A) -> B).andThen(action: (B) -> C): (A) -> C = { a: A -> action(this(a)) }

fun main() {
    val exp: (Int) -> Int = { x: Int -> x * x }
    val println: (Int) -> Unit = { i: Int -> println("Result: $i") }

    exp.andThen(println).invoke(5)
}
```

## Задания

Установить [EduTools](https://plugins.jetbrains.com/plugin/10081-edutools): `File` -> `Settings` -> `plugins`
-> `Marketplace` ->` EduTools`, перезагрузить IDE.

Начать обучение: `File` -> `Learn and Teach` -> `Browse Courсes` -> `Marketplace` -> в поиске выберете нужный курс.

* Базовый курс: _Kotlin Basic_.
* Полный курс по Kotlin: _AtomicKotlin_.

## Ссылки

1. [Руководство по языку Kotlin](https://kotlinlang.ru/)
3. [Learner start guide](https://plugins.jetbrains.com/plugin/10081-edutools/docs/learner-start-guide.html)