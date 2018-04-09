const json5 = require('json5');

const strToJS = (str, firstChar, lastChar) => {
  const arr = str.split('\n');

  let inc = 0;
  for (let i = 0; i <= arr.length - 1; i += 1) {
    if (arr[i].includes(firstChar)) {
      const newStr = arr[i].slice(arr[i].lastIndexOf(firstChar));
      arr[i] = newStr;
      break;
    }
    inc += 1;
  }
  arr.splice(0, inc);

  for (let i = arr.length - 1; i >= 0; i -= 1) {
    if (arr[i].includes(lastChar)) {
      const newStr = arr[i].slice(0, arr[i].indexOf(lastChar) + 1);
      arr[i] = newStr;
      break;
    }
    arr.pop();
  }

  return json5.parse(arr.join('\n'));
};
const stringObjToJS = str => strToJS(str, '{', '}');
const stringArrayToJS = str => strToJS(str, '[', ']');

module.exports = {
  strToJS,
  stringArrayToJS,
  stringObjToJS,
};
