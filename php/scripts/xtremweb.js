
function AnySelected()
{
  for (i = 0; i < document.sqlRows.elements.length; i++) {
    if (document.sqlRows.elements[i].checked) return true;
  }
  return false;
}

function toggle(isChecked)
{
  for (i = 0; i < document.sqlRows.elements.length; i++) {
    document.sqlRows.elements[i].checked = isChecked;
  }
}

function Submit(toDo)
{
  if (AnySelected()) {
    document.sqlRows.V_Action.value = toDo;
    document.sqlRows.submit();
  } else {
    window.alert('You must first select at least one row.');
  }
}

function flagMessages()
{
  if (document.select1.flag.options[document.select1.flag.selectedIndex].value != "")
    if (AnySelected()) {
      document.messages.flag.value = document.select1.flag.options[document.select1.flag.selectedIndex].value;

      document.messages.V_Action.value = 147;
      document.messages.submit();
    } else {
      if (whichForm == 1) {
				document.select1.flag.selectedIndex = 0;
      } else {
				document.select2.flag.selectedIndex = 0;
      }
      window.alert('You must select at least one message first.');
    }
}



var Flags;

Flags = new Array("33", "0", "1", "32", "0", "0");

function selectFlagged(flag, val)
{
  shift = 0;
  for (var i = 0; i < document.sqlRows.elements.length; i++) {
    while (document.sqlRows.elements[i].name != "indices[]") {
      i++;
      shift++;
      if (!document.sqlRows.elements[i]) {
				return;
      }
    }
    if (flag & Flags[i - shift]) {
      document.sqlRows.elements[i].checked = val;
    } else {
      document.sqlRows.elements[i].checked = !val;
    }
  }
}

function makeSelection ()
{
  flag = document.select1.filter.options[document.select1.filter.selectedIndex].value;

  if (flag.substring(0, 1) == "!") {
    selectFlagged(parseInt(flag.substring(1)), false);
  } else {
    selectFlagged(parseInt(flag), true);
  }

  document.select1.reset();
}
