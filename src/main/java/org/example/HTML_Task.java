package org.example;

import java.nio.file.Files;
import java.nio.charset.Charset;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Collection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.io.File;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;

//
// Decompiled by Procyon v0.5.36
//

public class HTML_Task implements Comparable
{
    boolean RECURSIVE;
    String NAME;
    String DIRECTORY;
    String OUTPUT;
    String FILE_MASK;
    ArrayList<HTML_Step> STEPS;

    public HTML_Task(final String NAME, final String DIRECTORY, String OUTPUT, final String FILE_MASK, final boolean RECURSIVE, final ArrayList<HTML_Step> STEPS) {
        this.NAME = NAME;
        this.DIRECTORY = DIRECTORY;
        this.FILE_MASK = FILE_MASK;
        this.RECURSIVE = RECURSIVE;
        this.STEPS = STEPS;
        if (!OUTPUT.substring(OUTPUT.length() - 1).equals("/")) {
            OUTPUT += "/";
        }
        this.OUTPUT = OUTPUT;
    }

    public void PerformAction() throws IOException {
        Collections.sort(this.STEPS);
        System.out.println("Name: " + this.NAME);
        System.out.println(this.DIRECTORY + "/" + this.FILE_MASK);
        final File directory = new File(this.DIRECTORY);
        final String cannonicalDirectory = directory.getCanonicalPath();
        final Collection<File> files = this.getListOfAllMatchingFiles(this.DIRECTORY, this.FILE_MASK, this.RECURSIVE) ;
        System.out.println("     Total file matches: " + files.size());
        for (File targetFile : files) {
            String file = targetFile.getName();
            final String canonicalFilePath = targetFile.getCanonicalPath();
            file = this.substractParentFolder(cannonicalDirectory + File.separator, canonicalFilePath);
            System.out.println(" - Processing file: " + file);
            final String sourcefile = this.DIRECTORY + File.separator + file;
            final String destinationfile = this.OUTPUT + file;
            final Path path = Paths.get(destinationfile, new String[0]);
            final String directoriesToCreate = path.getParent().toString();
            this.createDirectories(directoriesToCreate);
            System.out.println("Output in: " + this.OUTPUT);
            final TemplateManager templateManager = new TemplateManager(sourcefile, destinationfile);
            for (final HTML_Step step : this.STEPS) {
                System.out.println("     Step: " + step.NAME);
                System.out.println("      - Submitting Action: " + step.ACTION + " -> " + step.HTML_TARGET_MATCH);
                if (step.ACTION.equals("INSERT_BEFORE")) {
                    final String replacement = readFile(step.HTML_REPLACEMENT, StandardCharsets.UTF_8);
                    templateManager.HTMLinsertBefore(step.HTML_TARGET_MATCH, replacement, step.WHICH);
                }
                else if (step.ACTION.equals("INSERT_AFTER")) {
                    final String replacement = readFile(step.HTML_REPLACEMENT, StandardCharsets.UTF_8);
                    templateManager.HTMLinsertAfter(step.HTML_TARGET_MATCH, replacement, step.WHICH);
                }
                else if (step.ACTION.equals("REPLACE_WITH")) {
                    final String replacement = readFile(step.HTML_REPLACEMENT, StandardCharsets.UTF_8);
                    templateManager.replaceHTMLcontentWith(step.HTML_TARGET_MATCH, replacement, step.WHICH);
                }
                else if (step.ACTION.equals("REPLACE")) {
                    final String replacement = readFile(step.HTML_REPLACEMENT, StandardCharsets.UTF_8);
                    templateManager.replaceHTMLcontent(step.HTML_TARGET_MATCH, replacement, step.WHICH);
                }
                else if (step.ACTION.equals("DELETE")) {
                    templateManager.deleteHTMLelement(step.HTML_TARGET_MATCH, step.WHICH);
                }
                else if (step.ACTION.equals("UNWRAP")) {
                    templateManager.unwrapHTMLelement(step.HTML_TARGET_MATCH, step.WHICH);
                }
                else if (step.ACTION.equals("WRAP")) {
                    templateManager.wrapHTMLelement(step.HTML_TARGET_MATCH, step.VALUE, step.WHICH);
                }
                else if (step.ACTION.equals("ATTR_REPLACE")) {
                    templateManager.replaceAttr(step.HTML_TARGET_MATCH, step.ATTR, step.VALUE, step.WHICH);
                }
                else if (step.ACTION.equals("ATTR_REPLACE_SUBSTR")) {
                    templateManager.replaceAttrSubstring(step.HTML_TARGET_MATCH, step.SUBSTR, step.ATTR, step.VALUE, step.WHICH);
                }
                else if (step.ACTION.equals("ADD_CLASS")) {
                    templateManager.addClassToMatch(step.HTML_TARGET_MATCH, step.VALUE, step.WHICH);
                }
                else if (step.ACTION.equals("REMOVE_CLASS")) {
                    templateManager.removeClassToMatch(step.HTML_TARGET_MATCH, step.VALUE, step.WHICH);
                }
                else if (step.ACTION.equals("TEXT_REPLACE")) {
                    templateManager.replaceText(step.HTML_TARGET_MATCH, step.VALUE, step.WHICH);
                }
                else if (step.ACTION.equals("TEXT_REPLACE_SUBSTR")) {
                    templateManager.replaceTextSubstring(step.HTML_TARGET_MATCH, step.SUBSTR, step.VALUE, step.WHICH);
                }
                else if (step.ACTION.equals("ATTR_REPLACE_JSONMAP")) {
                    templateManager.replaceTextJSONMAP(step.HTML_TARGET_MATCH, step.HTML_REPLACEMENT, step.ATTR, step.WHICH);
                }
                else if (step.ACTION.equals("APPEND")) {
                    final String replacement = readFile(step.HTML_REPLACEMENT, StandardCharsets.UTF_8);
                    templateManager.replaceHTMLappend(step.HTML_TARGET_MATCH, replacement, step.WHICH);
                }
                else {
                    if (!step.ACTION.equals("PREPEND")) {
                        continue;
                    }
                    final String replacement = readFile(step.HTML_REPLACEMENT, StandardCharsets.UTF_8);
                    templateManager.replaceHTMLprepend(step.HTML_TARGET_MATCH, replacement, step.WHICH);
                }
            }
            templateManager.Close();
        }
    }

    private String substractParentFolder(final String parentDir, final String fullFilePath) {
        if (fullFilePath != null && parentDir != null && fullFilePath.startsWith(parentDir)) {
            return fullFilePath.substring(parentDir.length());
        }
        return fullFilePath;
    }

    Collection<File> getListOfAllMatchingFiles(final String directoryName, final String mask, final boolean includeSubdirectories) {
        final File directory = new File(directoryName);
        return FileUtils.listFiles(directory, new WildcardFileFilter(mask), includeSubdirectories ? TrueFileFilter.INSTANCE : null);
    }

    static String readFile(final String path, final Charset encoding) throws IOException {
        final byte[] encoded = Files.readAllBytes(Paths.get(path, new String[0]));
        return new String(encoded, encoding);
    }

    boolean createDirectories(final String dirs) {
        File directory = null;
        boolean bool = false;
        directory = new File(dirs);
        bool = directory.mkdirs();
        return bool;
    }

    @Override
    public int compareTo(final Object o) {
        return this.NAME.compareTo(((HTML_Task)o).NAME);
    }
}
