select   apps_binaries.Application, apps_binaries.Arch, apps_binaries.binaryUID,
         datas.name as DataFile, datas.size as DataSize, hex(datas.accessRights) as AccessRights, datas.insertionDate
from
( select apps.name as Application, 'linux_ix86' as Arch, right(apps.linux_ix86URI, 36) as binaryUID
  from   apps
  union
  select apps.name, 'linux_amd64',  right(apps.linux_amd64URI,  36)
  from   apps
  union
  select apps.name, 'linux_x86_64', right(apps.linux_x86_64URI, 36)
  from   apps
  union
  select apps.name, 'macos_ix86',   right(apps.macos_ix86URI,   36)
  from   apps
  union
  select apps.name, 'macos_x86_64', right(apps.macos_x86_64URI, 36)
  from   apps
  union
  select apps.name, 'win32_ix86',   right(apps.win32_ix86URI,   36)
  from   apps
  union
  select apps.name, 'win32_amd64',  right(apps.win32_amd64URI,  36)
  from   apps
  union
  select apps.name, 'win32_x86_64', right(apps.win32_x86_64URI, 36)
  from   apps
  union
  select apps.name, 'java',         right(apps.javaURI,         36)
  from   apps
) as apps_binaries
left  join datas on datas.uid = binaryUID
where      binaryUID is not null
order by   Application, Arch;
