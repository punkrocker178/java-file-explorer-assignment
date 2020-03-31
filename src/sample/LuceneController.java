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

public class LuceneController {

    private String indexPath;

    private Analyzer analyzer;
    private IndexWriterConfig indexWriterConfig;
    private IndexWriter indexWriter;

    public LuceneController(String indexPath) {
        this.indexPath = indexPath;
        analyzer = new StandardAnalyzer();
        try {
            Directory dir = FSDirectory.open(Paths.get(indexPath));
            indexWriterConfig = new IndexWriterConfig(analyzer);
            indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            indexWriter = new IndexWriter(dir, indexWriterConfig);
        } catch (IOException ignored) {

        }
    }


    public void addFileToIndex(Path path) throws IOException {

        File file = path.toFile();

        Document document = new Document();

        FileReader fileReader = new FileReader(file);
        document.add(
                new TextField("contents", fileReader));
        document.add(
                new StringField("path", file.getPath(), Field.Store.YES));
        document.add(
                new StringField("filename", file.getName(), Field.Store.YES));

        if (indexWriter.getConfig().getOpenMode() == IndexWriterConfig.OpenMode.CREATE) {
            // New index, so we just add the document (no old document can be there):
            System.out.println("adding " + file);
            indexWriter.addDocument(document);
        }
    }

    public void indexDocs(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        if (Files.isDirectory(path)) {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    try {
                        addFileToIndex(file);
                    } catch (IOException ignore) {
                        // don't index files that can't be read.
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } else {
            addFileToIndex(path);
        }
    }

    public List<Document> searchFiles(String inField, String queryString) {
        try {
            indexWriter.close();
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
