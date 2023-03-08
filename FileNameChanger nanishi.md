# FileNameChanger nanishi

## prototype

プロトタイプ版です。

今後、改良予定です。

## summary

PDFファイル内の文字列に応じて、ファイル名を変更します。

ファイル名の命名方法は、事前にCSVファイルに設定します。

## operation

ファイル・エクスプローラー上でPDFファイルを右クリックし、「送る」の中の「nanishi.bat」を選択します。

## install

### 必要なファイル

[nanishi.jar](https://github.com/sue-tax/nanishi/tree/master/nanishi/nashisi.jar)

[config.csv](https://github.com/sue-tax/nanishi/tree/master/nanishi/config.csv)

[nanishi.bat](https://github.com/sue-tax/nanishi/tree/master/nanishi/nanishi.bat)

### インストール先のフォルダ

左下のWindowsマークを右クリックします。

「ファイル名を指定して実行」を選択します。

「名前」に「shell:sendto」を入力して、「OK」ボタンをクリックします。

開いたフォルダがインストール先のフォルダです。

`C:\Users\XXXXXXXX\AppData\Roaming\Microsoft\Windows\SendTo`となっている場合が多いと思われます。`XXXXXXXX`は、各自のユーザー名です。

### nanishi.batの修正

```
java -jar  C:\Users\XXXXXXXX\AppData\Roaming\Microsoft\Windows\SendTo\nanishi.jar  C:\Users\XXXXXXXX\AppData\Roaming\Microsoft\Windows\SendTo\config.csv %1
```

の中の

`C:\Users\XXXXXXXX\AppData\Roaming\Microsoft\Windows\SendTo`

（２か所）を上記のインストール先のフォルダ名に書き換えます。

## configuration file

設定ファイル名のデフォルトは`config.csv`ですが、ファイル名は変えられます。

ファイル名を変えた場合は、nanishi.bat内の`config.csv`を変更してください。

### 設定ファイルの内容

0, filename, 【変更ファイル名フォーマット】

【マッチ番号】, 【マッチ名称】, 【マッチパターン】, 【変換文字列フォーマット】

#### 変更ファイル名フォーマット

#### マッチ番号、マッチ名称

マッチ番号は1～9です。

マッチ名称は、自由に記載できます。マッチ番号ごとに分かりやすい内容を書くことをお勧めします。

#### マッチパターン

==説明未作成==

#### 変換文字列フォーマット

==説明未作成==

### 設定ファイルの記載例

```
0, filename, %2$_%3$
2. 書類名, 青色申告決算書, 青色申告決算書
2, 書類名, 仕訳日記帳, 51_仕訳日記帳
2, 書類名, 残(\s|　)*高(\s|　)*試(\s|　)*算(\s|　)*表, 53_仕訳日記帳
3, 法人名, 田中工業, 田中工業
3, 法人名, 鈴木食品, 鈴木食品
3, 個人名, 佐藤(\s|　)*太郎, 佐藤太郎
3, 個人名, (斎|斉)藤(\s|　)*花子, 斎藤花子
3, 個人名, (高|髙)橋(\s|　)*直美	%1$s橋直美
```

変更するファイル名は、マッチ番号2の書類名とマッチ番号3の法人名・個人名を、`_`（ハイフン）でつなげたものになります。`%2$`はマッチ番号2を示します。

PDFファイルの中に、`青色申告決算書`の文字列がどこかにあれば、書類名を`青色申告決算書`とします。

`仕訳日記帳`の文字列がどこかにあれば、書類名を`53_仕訳日記帳`とします。変換文字列フォーマットは、マッチパターンと異なっていても良いです。

マッチパターンは**正規表現**を使います。

`残(\s|　)*高(\s|　)*試(\s|　)*算(\s|　)*表`の`(\s|　)*`は間に空白がいくつか入っていてもマッチします。

`(斎|斉)藤(\s|　)*花子`の`(斎|斉)`は`斎`と`斉`のいずれにもマッチします。

変換文字列フォーマットの`%1$s`は、マッチパターンの１番目の`()`に合致した文字列に置き換えます。

最後の行の`%1$s橋直美`ならば、`高`か`髙`に置き換えます。

JAVAの正規表現、**formatメソッド**の機能を理解すれば、より複雑な設定ができます。

