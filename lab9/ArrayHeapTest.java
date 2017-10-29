import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class ArrayHeapTest {

    /** Basic test of adding, checking, and removing two elements from a heap */
    @Test
    public void simpleTest() {
        ArrayHeap<String> pq = new ArrayHeap<>();
        pq.insert("Qir", 2);
        pq.insert("Kat", 1);
        assertEquals(2, pq.size());

        String first = pq.removeMin();
        assertEquals("Kat", first);
        assertEquals(1, pq.size());

        String second = pq.removeMin();
        assertEquals("Qir", second);
        assertEquals(0, pq.size());
    }

    /** More test for change priority*/
    @Test
    public void testarrayheap() {
        ArrayHeap<String> fruit = new ArrayHeap<>();
        fruit.insert("Apple", 1);
        fruit.insert("Pear", 4);
        fruit.insert("Kiwi", 0);
        fruit.insert("Pineapple", 9);
        fruit.insert("Watermelon", 17);
        fruit.insert("Orange", 5);
        fruit.insert("Mango", 4);
        fruit.insert("Banana", 10);

        fruit.changePriority("Banana", 5);
        fruit.changePriority("Apple", 6);

        fruit.changePriority("Banana", 0);

        fruit.insert("Grape", 3);

        String first = fruit.peek().item();
        assertEquals("Kiwi", first);

        String firstr = fruit.removeMin();
        assertEquals("Kiwi", firstr);

        String second = fruit.removeMin();
        assertEquals("Banana", second);
    }
    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(ArrayHeapTest.class));
    }
}
