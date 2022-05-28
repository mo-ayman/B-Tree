package search_engine;

import b_tree.BTree;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchEngine implements ISearchEngine {
    // b-tree for storing the words and their document ids
    private BTree<String, Doc> bTree;

    public SearchEngine() {
        this.bTree = new BTree<>(4);
    }

    private void parseFile(String filePath, int flag) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new File(filePath));
        NodeList nodeList = document.getElementsByTagName("doc");
        int count = 0;
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                String id = element.getAttribute("id");
                String title = element.getAttribute("title");
                String text = element.getTextContent();

                // index the document
                if (flag == 0) {
                    HashMap<String, Integer> wordCount = new HashMap<>();
                    count++;
                    String[] words = text.split("\\s+");
                    for(String w : words) {
                        w = w.toLowerCase();
                        if(wordCount.containsKey(w)) {
                            wordCount.put(w, wordCount.get(w) + 1);
                        } else {
                            wordCount.put(w, 1);
                        }
                    }

                    bTree.insert(id, new Doc(id, title, text, wordCount));
                } else if (flag == 1) {
                    bTree.delete(id);
                    count++;
                }
            }
        }
        if (flag == 0)
            System.out.println("Total number of inserted documents: " + count);
        else
            System.out.println("Total number of deleted documents: " + count);
    }

    @Override
    public void indexWebPage(String filePath) throws ParserConfigurationException, IOException, SAXException {
        parseFile(filePath, 0);
    }

    @Override
    public void indexDirectory(String directoryPath) throws ParserConfigurationException, IOException, SAXException {
        // include all the files in the directory and in subdirectories
        File dir = new File(directoryPath);
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                indexDirectory(file.getAbsolutePath());
            } else {
                parseFile(file.getAbsolutePath(), 0);
            }
        }
    }



    @Override
    public void deleteWebPage(String filePath) throws ParserConfigurationException, IOException, SAXException {
        parseFile(filePath, 1);
    }

    @Override
    public List<ISearchResult> searchByWordWithRanking(String word) {
        // implement iterator for b-tree
        int count = 0;
        word = word.toLowerCase();
        HashMap<String, Integer> map = new HashMap<>();
        for(Doc doc : bTree) {
            count++;
            if(doc.getWordCount().containsKey(word)) {
                map.put(doc.getId(), doc.getWordCount().get(word));
            }
        }
        System.out.println("count number of indexed doc = " + count);
        // return map as a list of search results
        List<ISearchResult> results = new ArrayList<>();
        for(String key : map.keySet()) {
            results.add(new SearchResult(key, map.get(key)));
        }
        return results;
    }

    @Override
    public List<ISearchResult> searchByMultipleWordWithRanking(String sentence) {
        // split the sentence into words
        String[] senWords = sentence.split("\\s+");
        HashMap<String, Integer[]> map = new HashMap<>();
        for(Doc doc : bTree) {
            int count = -1;

            for (String word : senWords) {
                word = word.toLowerCase();
                count++;
                if (doc.getWordCount().containsKey(word)) {

                    if (!map.containsKey(doc.getId()))
                        map.put(doc.getId(), new Integer[senWords.length]);

                    map.get(doc.getId())[count] = doc.getWordCount().get(word);
                }
            }
        }
        // return map as a list of search results
        List<ISearchResult> results = new ArrayList<>();
        for(String key : map.keySet()) {
            results.add(new SearchResult(key, min(map.get(key))));
        }
        return results;
    }

    private int min(Integer[] arr) {
        int mn = (int) 1e9;
        for(int i = 0; i < arr.length; i++) {
            if(arr[i] != null && arr[i] < mn) {
                mn = arr[i];
            }
        }
        assert mn != (int) 1e9;
        return mn;
    }




    public int pre() {
        return bTree.preOrderTraversal();
    }
}
