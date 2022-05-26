import org.xml.sax.SAXException;
import search_engine.Doc;
import search_engine.ISearchEngine;
import search_engine.ISearchResult;
import search_engine.SearchEngine;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


public class Main {

    public static int mn(Integer a, Integer b) {
        return Math.min(a, b);
    }
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {


        SearchEngine engine = new SearchEngine();
//        engine.indexWebPage("Wikipedia Data Sample/wiki_01");
        engine.indexDirectory("Wikipedia Data Sample/");

//        int count = engine.pre();
//        System.out.println("count of traversed node preorder : " + count);
//        List<ISearchResult> results = engine.searchByWordWithRanking("also");
//        int totalRank = 0;
//        for (ISearchResult result : results) {
//            totalRank += result.getRank();
////            System.out.println(result.getId() + " " + result.getRank());
//        }
//        List<ISearchResult> res = engine.searchByMultipleWordWithRanking("also called");
//        for (ISearchResult result : res) {
////            totalRank += result.getRank();
//            System.out.println(result.getId() + " " + result.getRank());
//        }

        String word = "the is";
        List<ISearchResult> res2 = engine.searchByMultipleWordWithRanking(word);
        String[] words = word.split(" ");
        List<ISearchResult> res3 = engine.searchByWordWithRanking(words[0]);
        List<ISearchResult> res4 = engine.searchByWordWithRanking(words[1]);


        // load res3 to hashmap and res4 to hashmap
        HashMap<String, Integer> map3 = new HashMap<>();
        for (ISearchResult result : res3) {
            map3.put(result.getId(), result.getRank());


        }
        HashMap<String, Integer> map4 = new HashMap<>();
        for (ISearchResult result : res4) {
            map4.put(result.getId(), result.getRank());
        }
        // merge the two hashmap
        HashMap<String, Integer> map5 = new HashMap<>();
        for (String key : map3.keySet()) {
            if (map4.containsKey(key)) {
                map5.put(key, mn(map3.get(key), map4.get(key)));
            } else {
                map5.put(key, map3.get(key));
            }
        }

        // load res2 to hashmap
        HashMap<String, Integer> map2 = new HashMap<>();
        for (ISearchResult result : res2) {
            map2.put(result.getId(), result.getRank());
        }
        // compare map5 and map2
        for (String key : map5.keySet()) {
            if (!Objects.equals(map2.get(key), map5.get(key))) {
                System.out.println("error");
            }
        }
        System.out.println("success");

//        System.out.println("Total totalRank:  " + totalRank);

//        BTree<Integer, String> bTree = new BTree<>(4);
//        bTree.insert(1, "one");
//
//
//        bTree.insert(1, "loay");
//        bTree.insert(2, "loay1");
//        bTree.insert(3, "loay2");
//        bTree.insert(4, "loay3");
//        bTree.insert(10, "loay4");
//        bTree.insert(17, "loay5");
//        bTree.insert(19, "loay6");
//        bTree.insert(20, "loay7");
//        bTree.insert(34, "loay8");
//        bTree.insert(37, "loay9");
//        bTree.insert(38, "loay10");
//        bTree.insert(45, "loay11");
//        bTree.insert(96, "loay");
//        bTree.insert(100, "loay");
//        System.out.println("38 ->" + bTree.delete(38));
//        System.out.println("19 ->" + bTree.delete(19));
//        System.out.println("45 ->" + bTree.delete(45));
//        System.out.println("17 ->" + bTree.delete(17));
//        System.out.println("2 ->" +bTree.delete(2));
//        System.out.println("34 ->" + bTree.delete(34));
//        System.out.println("20 ->" + bTree.delete(20));
//        System.out.println("100 ->" + bTree.delete(100));
//        System.out.println("3 ->" + bTree.delete(3));
//        System.out.println("96 ->" +bTree.delete(96));
//        System.out.println("4 ->" + bTree.delete(4));
//        System.out.println("37 ->" +bTree.delete(37));
//        System.out.println("1 ->" +bTree.delete(1));
//        System.out.println("10 ->" +bTree.delete(10));



    }
}