
public class ArrayHeap<T> {

    /**
     * DO NOT CHANGE THESE METHODS.
     */
    /* An ArrayList that stores the nodes in this binary heap. */
    private int[] contents;
    private int length;

    /* A constructor that initializes an empty ArrayHeap. */
    public ArrayHeap(int k) {
        this.contents = new int[k+1];
        this.length = k+1;
    }

    public int size() {
        return contents.length;
    }

    /* Returns the node at index INDEX. */
    private int getNode(int index) {
        if (index >= length) {
            return -1;
        } else {
            return contents[index];
        }
    }

    /* Sets the node at INDEX to N */
    private void setNode(int index, int n) {
        while (index + 1 > length) {
            return;
        }
        contents[index] = n;
    }

    /* Swap the nodes at the two indices. */
    private void swap(int index1, int index2) {
        int node1 = getNode(index1);
        int node2 = getNode(index2);
        this.contents[index1] = node2;
        this.contents[index2] = node1;
    }


    /* Returns the index of the node to the left of the node at i. */
    private int getLeftOf(int i) {
        return 2 * i;
    }

    /* Returns the index of the node to the right of the node at i. */
    private int getRightOf(int i) {
        return 2 * i + 1;
    }

    /* Returns the index of the node that is the parent of the node at i. */
    private int getParentOf(int i) {
        return i / 2;
    }

    /* Adds the given node as a left child of the node at the given index. */
    private void setLeft(int index, int n) {
        setNode(getLeftOf(index), n);
    }

    /* Adds the given node as the right child of the node at the given index. */
    private void setRight(int index, int n) {
        setNode(getRightOf(index), n);
    }

    /**
     * Returns the index of the node with smaller priority. Precondition: not
     * both nodes are null.
     */
    private int max(int index1, int index2) {
        if (contents[index1] >= contents[index2]) {
            return index1;
        } else {
            return index2;
        }
    }

    /* Returns the Node with the largest priority value, but does not remove it
     * from the heap. */
    public int peek() {
        int sindex = 1;
        for (int i = 1; i < length; i++) {
            sindex = max(sindex, i);
        }
        return contents[sindex];
    }

    /* Bubbles up the node currently at the given index. */
    private void bubbleUp(int index) {
        if (getParentOf(index) > 0) {
            while (contents[index]
                    > contents[getParentOf(index)]) {
                swap(index, getParentOf(index));
                index = getParentOf(index);
                if (getParentOf(index) <= 0) {
                    break;
                }
            }
        }
    }

    /* Bubbles down the node currently at the given index. */
    private void bubbleDown(int index) {
        int left = getLeftOf(index);
        int right = getRightOf(index);
        int large;
        if (left >= length && right >= length) {
            return;
        } else if (left >= length) {
            large = right;
        } else if (right >= length) {
            large = left;
        } else {
            large = max(left, right);
        }

        if (large < length) {
            while (contents[index]
                    < contents[large]) {
                swap(index, large);
                index = large;
                left = getLeftOf(index);
                right = getRightOf(index);
                if (left >= length && right >= length) {
                    break;
                } else if (left >= length) {
                    large = right;
                } else if (right >= length) {
                    large = left;
                } else {
                    large = max(left, right);
                }
            }
        }

    }

    /* Inserts an item with the given priority value. Same as enqueue,
    or offer. */
    public void heapify(int[] a) {
        for (int i = 1; i < length; i++) {
            contents[i] = a[i-1];
            bubbleUp(i);
        }
    }

    /* Returns the element with the smallest priority value, and removes it from
     * the heap. Same as dequeue, or poll. */
    public int removeMax() {
        int n = peek();
        swap(1, length-1);
        length -= 1;
        bubbleDown(1);
        return n;
    }
}

