// 防抖工具（阶段 5 批 2，模块四）：搜索框"输入即过滤"用。
// 刻意不引第三方依赖（决策 W 的延续）；返回值带 cancel，供组件卸载时清理定时器，避免悬挂回调。
/**
 * 生成一个防抖函数：连续调用只在停止 wait 毫秒后执行最后一次。
 * @param {Function} fn 真正要执行的函数
 * @param {number} [wait] 静默期，默认 300ms
 * @returns {Function & {cancel: Function}} 防抖后的函数，附 cancel()
 */
export function debounce(fn, wait = 300) {
  let timer = null

  function debounced(...args) {
    if (timer) clearTimeout(timer)
    timer = setTimeout(() => {
      timer = null
      fn(...args)
    }, wait)
  }

  debounced.cancel = () => {
    if (timer) {
      clearTimeout(timer)
      timer = null
    }
  }

  return debounced
}
