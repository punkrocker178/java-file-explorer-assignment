package sample;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LuceneIndex {

    public static final String indexPath = "/home/osboxes/Desktop/index";
    public static final String basePath = "/home/osboxes/Desktop/NNLT";

    public static void main(String[] args) {

        try {
            Directory dir = FSDirectory.open(Paths.get(indexPath));
            StandardAnalyzer analyzer = new StandardAnalyzer();

            IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
            indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            IndexWriter indexWriter = new IndexWriter(
                    dir, indexWriterConfig);

            indexWriter.commit();
            indexDocs(indexWriter, basePath);

            indexWriter.close();

            System.out.println(Arrays.toString(searchFiles("contents", "tc2", analyzer).toArray()));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void addFileToIndex(IndexWriter writer, Path path) throws IOException {

        File file = path.toFile();

        Document document = new Document();

        FileReader fileReader = new FileReader(file);
        document.add(
                new TextField("contents", fileReader));
        document.add(
                new StringField("path", file.getPath(), Field.Store.YES));
        document.add(
                new StringField("filename", file.getName(), Field.Store.YES));

        if (writer.getConfig().getOpenMode() == IndexWriterConfig.OpenMode.CREATE) {
            // New index, so we just add the document (no old document can be there):
            System.out.println("adding " + file);
            writer.addDocument(document);
        }
    }

    public static void indexDocs(IndexWriter writer, String filePath) throws IOException {
        Path path = Paths.get(filePath);
        if (Files.isDirectory(path)) {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    try {
                        addFileToIndex(writer, file);
                    } catch (IOException ignore) {
                        // don't index files that can't be read.
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } else {
            addFileToIndex(writer, path);
        }
    }

    public static List<Document> searchFiles(String inField, String queryString , StandardAnalyzer analyzer) {
        try {
            Query query = new QueryParser(inField, analyzer).parse(queryString);
            Directory  indexDirectory = FSDirectory.open(Paths.get(indexPath));

            IndexReader indexReader = DirectoryReader.open(indexDirectory);
            IndexSearcher searcher = new IndexSearcher(indexReader);


            TopDocs topDocs = searcher.search(query, 10);
            return Arrays.stream(topDocs.scoreDocs).map(scoreDoc -> {
                try {
                    return searcher.doc(scoreDoc.doc);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }).collect(Collectors.toList());
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
