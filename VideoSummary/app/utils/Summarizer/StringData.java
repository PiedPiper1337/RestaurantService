package utils.Summarizer;


public class StringData {
    private String word;
    private double tf = 0;
    private double df = 0;
    private double tfIdf = 0;

    public StringData(String word) {
        this.word = word;
    }

    public double getDf() {
        return df;
    }

    public void setDf(double df) {
        this.df = df;
    }

    public double getTf() {
        return tf;
    }

    public void setTf(double tf) {
        this.tf = tf;
    }

    public double getTfIdf() {
        return tfIdf;
    }

    public void setTfIdf(double tfIdf) {
        this.tfIdf = tfIdf;
    }

    public String getWord() {
        return word;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StringData that = (StringData) o;

        return word.equals(that.word);
    }

    @Override
    public int hashCode() {
        return word.hashCode();
    }

    @Override
    public String toString() {
        return "StringData{" +
                "df=" + df +
                ", word='" + word + '\'' +
                ", tf=" + tf +
                ", tfIdf=" + tfIdf +
                '}';
    }
}
