import java.util.Iterator;
import java.util.NoSuchElementException;

class A implements Iterator<String> {
  
  public String next() { // Noncompliant
    if (!hasNext()){
      return null;
    }
    return "x";
  }
  
}

class B implements Iterator<String> {
  
  public String next() { // Noncompliant
    if (!hasNext()){
      throw new IllegalStateException();
    }
    return "x";
  }
  
}

class C implements Iterator<String> {
  
  public String next() {
    if (!hasNext()){
      throw new NoSuchElementException();
    }
    return "x";
  }
  
  public String next(String argument) {
    return "x";
  }
  
  public String notNext() {
    return "x";
  }
  
}

class D implements Iterator<String> {
  
  public String next() { // Noncompliant
    if (!hasNext()){
      Excption e = new RuntimeException();
      throw e;
    }
    return "x";
  }
  
}

class E { // Not an iterator
  
  public String next() { 
    if (!hasNext()){
      return null;
    }
    return "x";
  }
  
}

abstract class F implements Iterator<String>{
  public abstract String next();
}

class G implements Iterator<String> {
  
  private Iterator<String> iter;
  
  public String next() {
    return iter.next();
  }
  
}

class H implements Iterator<String> {
  
  public String next() {
    unknownMethod();
    return throwsNoSuchElementException();
  }
  
  public String throwsNoSuchElementException() throws NoSuchElementException {
    throw new NoSuchElementException();
  }
  
}

class I implements Iterator<String> {
  
  public String next() { // Noncompliant
    return throwsIndexOutOfBoundsException();
  }
  
  public String throwsIndexOutOfBoundsException() throws IndexOutOfBoundsException {
    throw new IndexOutOfBoundsException();
  }
  
}