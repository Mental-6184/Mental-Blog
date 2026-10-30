# 数据库与 SQL

## 推荐用法（新建库）

- 执行 [init.sql](../init.sql)（已包含完整表结构 + 必要初始化数据）

```bash
mysql -u root -p < init.sql
```

## 可选：导入示例数据

以下脚本都需要在执行 init.sql 后运行，按需选择其一即可：

- [seed_data.sql](../seed_data.sql)：内容更全（分类/标签/文章/评论/关联）
- [sample-data.sql](../sample-data.sql)：示例文章数据（文件较长）
- [sample_articles.sql](../MentalBlog-H/sample_articles.sql)：后端目录下的示例文章（偏旧，结构简单）

示例：

```bash
mysql -u root -p mentalblog < seed_data.sql
```

## 变更脚本（已有库升级）

如果你是从旧版本 SQL 初始化过数据库（例如历史上用过 description=VARCHAR(200)），可按需执行：

- [alter_descriptions.sql](../alter_descriptions.sql)：扩大 `friend_link.description`、`category.description`
- [alter_friend_link_description.sql](../alter_friend_link_description.sql)：同上（仅友链，和 alter_descriptions.sql 重复）

## 旧脚本说明（已合并进 init.sql）

以下脚本在当前版本中不再是必需步骤，主要用于排查/补丁或历史兼容：

- [about_tables.sql](../MentalBlog-H/about_tables.sql)：关于我（skill/project/milestone）建表，已合并进 init.sql
- [image_permission.sql](../MentalBlog-H/image_permission.sql)：图库权限字段+申请表，已合并进 init.sql
- [image_module.sql](../MentalBlog-H/image_module.sql)：图库相关表，已合并进 init.sql
- [fix_permission.sql](../MentalBlog-H/fix_permission.sql)、[fix_missing_tables.sql](../fix_missing_tables.sql)：用于“缺表/缺字段”时的补丁，已合并进 init.sql
