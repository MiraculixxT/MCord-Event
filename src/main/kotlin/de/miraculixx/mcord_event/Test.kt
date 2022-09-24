package de.miraculixx.mcord_event


fun main() {
    val tree = Tree(5, Tree(3, Tree(1, null, null), null),
        Tree(7, Tree(6, null, null), Tree(9, Tree(8, null, null), Tree(10, null,null))))

    println("Pre Order")
    preOrder(tree)

    println("\nPost Order")
    postOrder(tree)

    println("\nIn Order")
    inOrder(tree)
}

fun preOrder(tree: Tree?) {
    if (tree == null) return

    print("${tree.value}, ") // PRINT PRE (before)
    preOrder(tree.left)
    preOrder(tree.right)
}

fun postOrder(tree: Tree?) {
    if (tree == null) return

    postOrder(tree.left)
    postOrder(tree.right)
    print("${tree.value}, ") // PRINT POST (after)
}

fun inOrder(tree: Tree?) {
    if (tree == null) return

    inOrder(tree.left)
    print("${tree.value}, ") // PRINT IN (between)
    inOrder(tree.right)
}

data class Tree(val value: Int, val left: Tree?, val right: Tree?)

