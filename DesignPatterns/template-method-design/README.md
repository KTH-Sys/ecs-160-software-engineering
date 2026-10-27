# Template Method design pattern

Study example based on **Lecture 4 and DesignPatterns.pdf, slides 94–100**. The simplified code, explanation, and analogy were created for studying; they are not the professor's original code.

## Problem statement

The course example finds paths from a source node to a destination node in a graph represented by a file. Both BFS-based and DFS-based implementations follow this outline:

```text
Initialize the graph → Traverse the graph → Collect and print results
```

The traversal differs, while the shared workflow can be reused. We want subclasses to customize that step without duplicating the full algorithm outline.

[TemplateMethodExample.java](TemplateMethodExample.java) demonstrates the method sequence through printed actions. Graph loading, BFS/DFS path searching, and result collection are placeholders; this study file is not a complete graph-path solver.

## Analogy: a recipe outline

A recipe can specify:

```text
Prepare ingredients → Cook → Serve
```

Rice and pasta require different cooking procedures, but can follow the same overall outline. The parent provides the outline, and subclasses supply the differing step.

## Naïve approach

Write the whole workflow separately for BFS and DFS:

```text
BFS version: initialize → BFS traversal → collect results
DFS version: initialize → DFS traversal → collect results
```

This duplicates the shared steps and their ordering. A change to initialization or result handling must be maintained in both versions.

## Template Method solution

The parent class defines the template method:

```java
public final void findPaths(int source, int destination) {
    this.source = source;
    this.destination = destination;

    initialize();
    traverseGraph();
    collectResults();
}
```

`findPaths()` is the template method because it defines the sequence of operations. `traverseGraph()` is one customizable step within that sequence.

| Part | Implementation in the example |
| --- | --- |
| Template method | `GraphPathFinder.findPaths()` |
| Shared concrete steps | `initialize()` and `collectResults()` |
| Required abstract step | `traverseGraph()` |
| Concrete variants | BFSPathFinder and DFSPathFinder |

The parent declares `protected abstract void traverseGraph()`. Each concrete subclass must implement it. The source and destination fields are protected so subclasses can use the values stored by findPaths.

## Trace the calls

```java
GraphPathFinder finder = new BFSPathFinder();
finder.findPaths(1, 100);
```

The variable's declared type is GraphPathFinder, and the actual object is a BFSPathFinder. It inherits findPaths from its parent.

```text
GraphPathFinder.findPaths(1, 100)
    → GraphPathFinder.initialize()
    → BFSPathFinder.traverseGraph()
    → GraphPathFinder.collectResults()
```

The call to traverseGraph appears inside the parent method, but runtime polymorphism selects the actual object's override. These methods all run on the same BFSPathFinder object; no separate parent object is created.

A DFSPathFinder follows the same sequence, selecting DFSPathFinder.traverseGraph for the middle step.

The lecture calls this inverted control: the parent organizes the workflow and calls the subclass's customized behavior when needed.

## Why use final?

The template method is final, so subclasses cannot override the whole findPaths method and replace its sequence. They customize the individual steps it calls.

The provided initialize and collectResults methods are not final in this example. They have default implementations and may also be overridden. Their existing bodies mean a concrete subclass is not required to reimplement them.

## Step types and hooks

| Step type | Parent supplies | Subclass responsibility |
| --- | --- | --- |
| Concrete operation | A working default implementation | Inherit it or override if permitted |
| Abstract operation | A method declaration without a body | Implement it in a concrete subclass |
| Hook | An optional callback with an empty or default implementation | Override only when customization is needed |

A hook is an optional extension point called by the template. The runnable example uses shared concrete steps and one abstract step; it does not add a separate hook.

## Why use this pattern?

- Reuse the overall algorithm structure and shared steps.
- Keep variations focused on the operations that differ.
- Centralize the sequence so it is easier to maintain consistently.
- Use the abstract-method contract to catch missing required implementations at compile time.

This is a behavioral pattern. It organizes an algorithm and the responsibilities of the parent and subclasses. It uses inheritance for customization; the example needs no wrapper object.

## Run the example

With Java 11 or later, run from this folder:

```sh
java TemplateMethodExample.java
```

Expected output:

```text
Initializing graph
BFS traversal from 1 to 100
Collecting results
Initializing graph
DFS traversal from 1 to 100
Collecting results
```

The output demonstrates which methods execute and in what order. It does not list actual graph paths.

## Check your understanding

1. Which method is the template, and which is the abstract step?
2. Why does the parent method's traversal call select the BFS implementation for a BFSPathFinder object?
3. What would removing final allow a subclass to do?
4. Why does a concrete subclass need to implement traverseGraph but not initialize?
