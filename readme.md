* PhotoByIntent
N予備校「写真撮影」学習用プロジェクト

写真撮影用のインテントアクション
MediaStore.ACTION_IMAGE_CAPTURE

** Manifest Declaration
<uses-permission android:name="android.permission.CAMERA" />
パーミッションandroid.permission.CAMERAはDANGEROUS PERMISSION なので、
API23以上では利用許可を求めるコードが必要。
インテントでカメラアプリを起動するだけなら記述不要。

<uses-feature android:name="android.hardware.camera" />
この記述によりカメラを持っていないデバイスからのダウンロードを防ぐことができる。
