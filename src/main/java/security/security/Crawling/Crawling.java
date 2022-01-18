package security.security.Crawling;

public interface Crawling {

    public String url = "";
    public int page = 10;
    public boolean action = false;

    public void setPage(int page);
    public void craw() throws InterruptedException;


}
