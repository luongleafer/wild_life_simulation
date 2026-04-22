# Tài liệu thiết kế

## Ý tưởng chung

### Địa hình thế giới

Chương trình mô phỏng một thế giới 2D với các loài động vật hoang dã.

Mô hình thế giới được tạo nên từ các khối (block) có chiều dài 1 đơn vị, chiều rộng 1 đơn vị, được đặt tại các vị trí là số nguyên trên một lưới.

Lớp `BlockModel` biểu diễn mô hình một khối. Mỗi khối sẽ chứa thông tin về vị trí của mình và trạng thái hiện tại. Vị trí một khối được biểu diễn bởi hai số nguyên (x,y), được tách ra thành lớp `BlockCoordinate`. Tất cả các loại khối được sử dụng trong chương trình là lớp con của `BlockModel`, với mỗi lớp định nghĩa riêng một giá trị `blockType` duy nhất để phân biệt với các khối khác.
Tạm thời định nghĩa trạng thái khối là một số nguyên.

Lớp `BlockView` phụ trách thể hiện khối trên màn hình. Lớp này chứa một map từ `BlockModel` đến một dãy các thể hiện của khối theo từng trạng thái.
(Lớp này có thể được thể hiện dưới dạng [Singleton](https://refactoring.guru/design-patterns/singleton) )
Ví dụ, nếu BlockView chứa cặp sau: `"dirt", {"a", "b","c"}`, thì nếu khối đất ở trạng thái `0`, xâu `"a"` được hiển thị trên màn hình.

Lớp `BiomeModel` biểu thị một quần xã. Quần xã là tập hợp các điều kiện tự nhiên (các khối) và các sinh vật có mối quan hệ với nhau. Mỗi lớp kế thừa `BiomeModel` cần định nghĩa các khối có thể xuất hiện trong quần xã đó (`blockPalette`). Vị trí phân bố quần xã được xác định bởi hai tọa độ `topLeft` và `bottomRight` đánh dấu một khu vực hình chữ nhật trên thế giới. Mỗi quần xã sẽ phụ trách việc xây dựng địa hình của chính quần xã đó bằng phương thức `generate()`. Phương thức này tạo và trả về một danh sách các khối được tạo ra. Việc phân bố khối là tùy thuộc theo quần xã, có thể bao gồm, nhưng không giới hạn các cách đặt khối như sau:

- Toàn bộ quần xã chỉ có một khối duy nhất
- Một khối được đặt làm nền của quần xã, các khối còn lại được đặt rải rác từng khối một
- Một khối được đặt làm nền của quần xã, các khối còn lại được đặt thành các cụm (blob) rải rác trên quần xã.

Tạm thời `blockPalette` là một mảng không phân biệt, nên khi triển khai từng quần xã có thể tùy ý. Nếu có thởi gian có thể triển khai `blockPalette` phức tạp hơn với nhiều phương thức phân bố khối hơn.

Lớp `BiomeView` sẽ vẽ quần xã dưới dạng một hình chữ nhật, tách biệt với các quần xã khác.

Lớp `WorldModel` biểu diễn thế giới. Lớp này phụ trách một mảng hai chiều thông tin về các khối, một danh sách các quần xã xuất hiện trong nó. Khi tạo thế giới, nó sẽ sinh ra các khu vực cho các quần xã, rồi xây dựng từng quần xã và ghép lại. Phương thức `update()` được dùng để cập nhật trạng thái các khối bên trong thế giới. Phương thức `placeBlock()` được dùng khi muốn đặt khối từ bên ngoài.

Lớp `WorldView` vẽ thế giới dưới dạng một hình chữ nhật lớn, với nhiều quần xã và các khối.

Tùy thuộc theo các thức vẽ khối mà cách vẽ quần xã và thế giới cũng khác nhau.
