# GökDil - Türkçe Programlama Dili

<img src="src/resources/logo.png" width="200" height="200" alt="GökDil Logo">

GökDil, Türkçe sözdizimi ile programlama yapmayı sağlayan, eğitim amaçlı geliştirilmiş bir programlama dilidir. Temel programlama kavramlarını Türkçe ifadelerle öğretmeyi amaçlar.

GökDil is an educational programming language developed to enable programming using Turkish syntax. It aims to teach fundamental programming concepts with Turkish expressions.

## Özellikler/Features

### 1. Türkçe Anahtar Kelimeler / Turkish Keywords
- `tanımla`: Değişken tanımlama / Variable declaration
- `tür`: Değişken türü belirtme / Specify variable type
- `başlangıç`: Değişken başlangıç değeri atama / Assign initial value
- `ise`: Koşul ifadeleri / Conditional statement (`if`)
- `değilse`: Alternatif koşul / Else statement (`else`)
- `böyleyken`: Döngü yapısı / Loop structure (`while`)
- `yazdır`: Ekrana çıktı verme / Print output (`print`)
- `fonksiyon`: Fonksiyon tanımlama / Function definition
- `dön`: Fonksiyondan değer döndürme / Return value

### 2. Veri Tipleri / Data Types
- `tamsayı`: Tam sayı değerler / Integer numbers
- `ondalık`: Ondalıklı sayılar / Floating point numbers
- `yazı`: Metin değerleri / Text strings

### 3. Operatörler / Operators
- Aritmetik / Arithmetic : `+`, `-`, `*`, `/`, `%`
- Karşılaştırma / Comparison: `>`, `<`, `>=`, `<=`, `==`
- Mantıksal / Logical: `ve`, `veya`
- Atama / Assignment: `=`

### 4. Kontrol Yapıları / Control Structures
```
ise (koşul) {
    // kod bloğu
} değilse {
    // kod bloğu
}

böyleyken (koşul) {
    // döngü bloğu
}
```

### 5. Fonksiyonlar / Functions
```
fonksiyon topla(a, b) {
    dön a + b$
}
```

## Örnek Kod / Example Code

```
@ Değişken tanımlama
tanımla sayi1 tür tamsayı başlangıç 42$
tanımla metin tür yazı başlangıç "Merhaba GökDil"$

@ Koşullu ifade
ise (sayi1 > 40) {
    yazdır("sayi1 40'tan büyük")$
} değilse {
    yazdır("sayi1 40'tan küçük veya eşit")$
}

@ Döngü örneği
tanımla sayac tür tamsayı başlangıç 0$
böyleyken (sayac < 3) {
    yazdır(sayac)$
    sayac = sayac + 1$
}
```

## Özel Sözdizimi Özellikleri / Special Syntax Features

1. Her ifade sonunda `$` işareti kullanılır
2. Yorum satırları `@` işareti ile başlar
3. Metin değerleri çift tırnak içinde yazılır
4. Bloklar süslü parantez `{}` içinde tanımlanır

1. Each statement ends with `$`
2. Comments start with `@`
3. String values are enclosed in double quotes
4. Blocks are defined within curly braces `{}`

## Kullanım / Usage

1. Test dosyalarınızı `.txt` uzantısı ile kaydedin
2. GökDil GUI uygulamasını başlatın
3. "Test Dosyası Seç" butonuna tıklayın
4. Çalıştırmak istediğiniz dosyayı seçin

1. Save your test files with `.txt` extension
2. Launch the GökDil GUI application
3. Click "Select Test File" button
4. Choose the file you want to run

## Geliştirme Ortamı / Development Environment

GökDil, Java programlama dili kullanılarak geliştirilmiştir. Temel bileşenleri:

- Lexer: Kaynak kodu tokenlara ayırır
- Parser: Tokenleri sözdizimi ağacına dönüştürür
- Interpreter: Sözdizimi ağacını yorumlayarak kodu çalıştırır
- GUI: Kullanıcı dostu grafiksel arayüz

GökDil is developed in Java with core components:
- Lexer: Tokenizes source code
- Parser: Converts tokens into syntax tree
- Interpreter: Executes syntax tree
- GUI: User-friendly graphical interface

## Test Dosyaları / Test Files

- `dogru1.txt`: Temel özelliklerin örnekleri
- `dogru2.txt`: İleri seviye özellikler
- `hatali1.txt` ve `hatali2.txt`: Hata örnekleri

- `dogru1.txt`: Basic feature examples
- `dogru2.txt`: Advanced features
- `hatali1.txt` and `hatali2.txt`: Error examples

## Hata Yönetimi / Error Handling

GökDil, programlama hatalarını Türkçe olarak bildirir:
- Sözdizimi hataları
- Tip uyumsuzlukları
- Tanımsız değişken kullanımı
- Sıfıra bölme hataları

GökDil reports programming errors in Turkish:
- Syntax errors
- Type mismatches
- Undefined variable usage
- Division by zero


## Katkıda Bulunma / Contributing

GökDil, açık kaynaklı bir projedir. Katkılarınızı bekliyoruz:
1. Yeni özellikler ekleyin
2. Hataları düzeltin
3. Dokümantasyonu geliştirin
4. Test dosyaları oluşturun

GökDil is open source. Contributions are welcome:
- Add new features
- Fix bugs
- Improve documentation
- Create test files


