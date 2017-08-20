/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graph;

/**
 *
 * @author Stefan
 */
public class ProgramFile {
    
    private String fileName;
    private boolean fileOpened;
    private String [] programsBody;

    public ProgramFile(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isFileOpened() {
        return fileOpened;
    }

    public void setFileOpened(boolean fileOpened) {
        this.fileOpened = fileOpened;
    }

    public String[] getProgramsBody() {
        return programsBody;
    }

    public void setProgramsBody(String[] programsBody) {
        this.programsBody = programsBody;
    }
    
    
}
