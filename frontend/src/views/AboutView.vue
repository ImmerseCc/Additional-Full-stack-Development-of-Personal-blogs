<script setup>
// 关于本站（阶段 3 建为占位页；阶段 5 批 5 落地为完整页，模块六）：
// 说明项目定位、技术栈与前后端数据分工，并挂上"本地数据"面板（概览 + 一键重置）。
// 本页不发后端请求：概览只读 localStorage，重置只清本机数据。
import LocalDataPanel from '@/components/LocalDataPanel.vue'
</script>

<template>
  <section class="page about">
    <header class="about__head">
      <h1>关于本站</h1>
      <p class="about__lead">
        这是一个<strong>从零手写</strong>的个人博客全栈项目，用于学习与实战演练：<strong>未使用任何现成博客模板或整站主题</strong>，
        所有页面、组件与接口均为自行实现。
      </p>
    </header>

    <section class="about__section" aria-labelledby="about-stack">
      <h2 id="about-stack">技术栈</h2>
      <ul class="about__list">
        <li>
          前端：Vue 3（Composition API + <code>&lt;script setup&gt;</code>）+ Vite + Vue Router + Pinia，
          纯 JavaScript，原生 CSS 变量主题（亮 / 暗 / 跟随系统）
        </li>
        <li>后端：Spring Boot 4.1.1 + JdbcTemplate + SQLite，统一返回体 <code>{ code, message, data }</code></li>
        <li>Markdown：markdown-it + highlight.js + DOMPurify（先渲染再过滤，正文与外链都有防护）</li>
        <li>接口契约与数据模型：见仓库 <code>docs/api-contract.md</code>、<code>docs/data-model.md</code></li>
      </ul>
    </section>

    <section class="about__section" aria-labelledby="about-data">
      <h2 id="about-data">数据放在哪</h2>
      <p>
        文章、评论与点赞计数的事实来源都在后端，存在服务端的 SQLite 单文件里；浏览器这边只保留「本机偏好」与「本机状态」：
        主题选择、访客标识（<code>visitorId</code>）、你点过赞的文章、你发过的评论，以及第一次评论时填下的昵称与邮箱。
      </p>
      <p>
        所以清空浏览器本地数据不会删掉任何文章或评论，只会让你「换一个人」：之前点过的赞仍计入总数，
        但本机不再显示为已点赞 —— 这是演示级方案，公网部署需要换成真正的鉴权。
      </p>
    </section>

    <LocalDataPanel />
  </section>
</template>

<style scoped>
.about__head {
  margin-bottom: 28px;
}

.about__head h1 {
  margin: 0 0 12px;
  font-size: 32px;
}

.about__lead {
  max-width: 68ch;
  margin: 0;
  color: var(--color-muted);
}

.about__section {
  margin-bottom: 28px;
}

.about__section h2 {
  margin: 0 0 12px;
  font-size: 22px;
}

.about__section p {
  max-width: 68ch;
  margin: 0 0 12px;
}

.about__list {
  max-width: 68ch;
  margin: 0;
  padding-left: 20px;
  color: var(--color-muted);
}

.about__list li + li {
  margin-top: 6px;
}
</style>
